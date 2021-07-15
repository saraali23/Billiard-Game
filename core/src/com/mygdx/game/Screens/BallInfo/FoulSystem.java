package com.mygdx.game.Screens.BallInfo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.Screens.EndGameScreen;
import com.mygdx.game.Screens.GameScreen;

public class FoulSystem {
    public boolean checkfirstballtouched=false;
    public boolean checkfirstround=false;
    public boolean checknoballs=false;
    public static boolean checkchangetheplayerturn=false;
    public static int foulnumber=0;


    public void Checkfouls() // checks if there is any foul and commits it
    {
        if(GameScreen.turnfinished) // if the round just started it will enter here
        {
            System.out.println(foulnumber);
            System.out.println("player 1 : "+ GameScreen.player1.isSolidBall()+" " + GameScreen.player1.isStripeBall());
            System.out.println("player 2 : "+GameScreen.player2.isSolidBall()+" " + GameScreen.player2.isStripeBall());

            if(foulnumber==0) { // if the ball didnt touch anything in the last round it will put the ball in hands and changes the turn
                GameScreen.checkcueball = true;
                checkchangetheplayerturn=false;
                Player.playeroneturn= !Player.playeroneturn;
                System.out.println("the cueball didnt touch anyball");
            }
            else if (Player.playeroneturn&&!GameScreen.player1.isCheckdropedaball()) // if its player 1 turn and didnt put any balls in the hole but doesnt put the ball in hands
            {
                checkchangetheplayerturn=false;
                Player.playeroneturn= !Player.playeroneturn;
                checknoballs=true;
                System.out.println("noballs1");

            }
            else if (!Player.playeroneturn&&!GameScreen.player2.isCheckdropedaball()) // same for player2
            {
                checkchangetheplayerturn=false;
                Player.playeroneturn= !Player.playeroneturn;
                checknoballs=true;
                System.out.println("noballs2");
            }
            else if(checkfirstround)
                foulnumber=5; // return things to its default

            checkchangetheplayerturn=false;
            GameScreen.turnfinished=false;// so it doesnt enter this function again

        }

        checkfirstballtouched=false; // return things to its default

    }

    public void Detectifanyfoulsishappening() // checks if there is a foul while the balls are moving
    {
        if(checkfirstballtouched==false&&checkfirstround) //if there is no balls touched yet and its not the first round
        {

            if(Player.playeroneturn) // for player 1 only
            {
                if(foulnumber==1&& GameScreen.player1.getBalls_in_pocket()!=7) // if the player touched the black and he didnt put his 7 balls in hole
                {
                    GameScreen.checkcueball=true; //puts the ball in hands
                    System.out.println("the player1 touched the black");
                }
                else if (foulnumber==2&& !GameScreen.player1.isSolidBall()) // if the player touched the other player balls
                {
                    GameScreen.checkcueball=true;//puts the ball in hands
                    //heree
                    System.out.println("the player1 touched the solid");

                }
                else if (foulnumber==3&& !GameScreen.player1.isStripeBall()) // if the player touched the other player balls
                {
                    GameScreen.checkcueball=true;
                    System.out.println("the player1 touched the stripe");

                }
                else
                {
                    GameScreen.checkcueball=false; // returns things to the default
                }



            }
            else // same things for player 2

            {
                if(foulnumber==1&&GameScreen.player2.getBalls_in_pocket()!=7)
                {
                    GameScreen.checkcueball=true;
                    //here
                    System.out.println("the player2 touched the black");

                }
                else if (foulnumber==2&& !GameScreen.player2.isSolidBall())
                {
                    GameScreen.checkcueball=true;
                    //heree
                    System.out.println("the player2 touched the solid");

                }
                else if (foulnumber==3&& !GameScreen.player2.isStripeBall())
                {
                    GameScreen.checkcueball=true;
                    //here
                    System.out.println("the player2 touched the stripe");

                }
                else
                {
                    GameScreen.checkcueball=false;
                }

            }


            if(foulnumber!=0) // if the player touched any balls
                checkfirstballtouched=true; // returns things to the default
        }




    }

    public void Checkifanyballtouchestheholes() // checks if any ball touched the holes
    {

        GameScreen.world.getBodies(GameScreen.tmpBodies);

        for(Body body:GameScreen.tmpBodies) // loops over all bodies in the world
        {

            if((body.getPosition().y>=8.6f||body.getPosition().y<=-8.6f||body.getPosition().x>=17.4||body.getPosition().x<=-18.7f)&&body.getLinearDamping()!=0) { //if the balls touched the holes

                if(body.equals(GameScreen.table.getCueball().getBall())) // if the body is the cueball
                {

                    GameScreen.table.getCueball().getBall().setLinearVelocity(0,0);
                    GameScreen.table.getCueball().getBall().setTransform(100,100,0); // transforms it out of the screen
                    GameScreen.checkcueball=true; // a foul and will put the ball in hands next round

                    System.out.println("you dropped the cueball in the hole");
                    if(!checknoballs) { // if there is two fouls this will reduce it to 1 so the turn doesnt get changed twice
                        checkchangetheplayerturn = true; //changes the turn
                        checknoballs=false; // returns things to the default
                    }
                }
                else {

                    if(body.equals(GameScreen.table.getBlackball().getBall())&&!GameScreen.table.getBlackball().isIsout()) { // when the blackball enters the hole

                          GameScreen.table.getBlackball().setIsout(true); //so it doesnt enter here again
                          ((Game)(Gdx.app.getApplicationListener())).setScreen(new EndGameScreen()); // endgame screen
                    }

                    for(int i=0;i<7;i++) // loops over all the 14 ball and checks if anyball touched the holes
                    {

                        if(body.equals(GameScreen.table.getSolidball()[i].getBall())) {// so it doesnt enter again
                            if(GameScreen.table.getSolidball()[i].isIsout())
                                continue;


                            body.setLinearVelocity(0,0); // to put the ball to the balls bar on the right
                            body.setAngularVelocity(0);

                            body.setTransform(21.7f, GameScreen.y, 0);
                            GameScreen.y+=1; // a counter to put the balls on the right bar on  horizontal


                            GameScreen.table.getSolidball()[i].setIsout(true); //to fix the bug
                            body.setLinearVelocity(0,0);// gives the ball no physics
                            body.setAngularVelocity(0);//set velocity to zero
                            //
                            if(Player.playeroneturn &&GameScreen.player1.getBalls_in_pocket()==0&&GameScreen.player2.getBalls_in_pocket()==0)// if no one chose the type of balls yet
                            {//if none of the players entered a ball
                                GameScreen.player1.setSolidBall(true); // gives player 1 this type of balls
                                GameScreen.player2.setStripeBall(true);// give the other player the other type of balls
                                checkfirstround=true; // the first round ended
                            }
                            // same but for player 2
                            else if (!Player.playeroneturn &&GameScreen.player2.getBalls_in_pocket()==0&&GameScreen.player1.getBalls_in_pocket()==0)
                            {
                                GameScreen.player2.setSolidBall(true);
                                GameScreen.player1.setStripeBall(true);
                                checkfirstround=true;
                            }

                            if(GameScreen.player1.isSolidBall()) { // if the ball that was droped is solid it raises the number of balls he entered
                                GameScreen.player1.setBalls_in_pocket(GameScreen.player1.getBalls_in_pocket() + 1);
                                if(Player.playeroneturn)
                                {
                                    GameScreen.player1.setCheckdropedaball(true); // tells that the player entered a ball this round at least
                                    System.out.println("player1true1");

                                }

                            }
                            //same but for player 2
                            else if (GameScreen.player2.isSolidBall()) {
                                GameScreen.player2.setBalls_in_pocket(GameScreen.player2.getBalls_in_pocket() + 1);
                                if(!Player.playeroneturn)
                                {
                                    GameScreen.player2.setCheckdropedaball(true);
                                    System.out.println("player2true1");

                                }
                            }


                        }



                        // same all the above but for the stripe balls
                        else if (body.equals(GameScreen.table.getStripeball()[i].getBall())) {
                            if(GameScreen.table.getStripeball()[i].isIsout())
                                continue;


                            body.setLinearVelocity(0,0);
                            body.setAngularVelocity(0);

                            body.setTransform(21.7f, GameScreen.y, 0);
                            GameScreen.y+=1;

                            GameScreen.table.getStripeball()[i].setIsout(true);
                            body.setLinearVelocity(0,0);
                            body.setAngularVelocity(0);

                            if(Player.playeroneturn &&GameScreen.player1.getBalls_in_pocket()==0&&GameScreen.player2.getBalls_in_pocket()==0)
                            {
                                GameScreen.player1.setStripeBall(true);

                                GameScreen.player2.setSolidBall(true);
                                checkfirstround=true;
                            }
                            else if (!Player.playeroneturn &&GameScreen.player2.getBalls_in_pocket()==0&&GameScreen.player1.getBalls_in_pocket()==0)
                            {
                                GameScreen.player2.setStripeBall(true);

                                GameScreen.player1.setSolidBall(true);
                                checkfirstround=true;
                            }

                            if(GameScreen.player1.isStripeBall()) {
                                GameScreen.player1.setBalls_in_pocket(GameScreen.player1.getBalls_in_pocket() + 1);
                                if(Player.playeroneturn)
                                {
                                    GameScreen.player1.setCheckdropedaball(true);
                                    System.out.println("player1true2");

                                }

                            }
                            else if (GameScreen.player2.isStripeBall()) {
                                GameScreen.player2.setBalls_in_pocket(GameScreen.player2.getBalls_in_pocket() + 1);
                                if(!Player.playeroneturn)
                                {
                                    GameScreen.player2.setCheckdropedaball(true);
                                    System.out.println("player2true2");

                                }
                                //else System.ot.pritln("Not player 2 turn");
                            }


                        }

                    }

                }
            }

        }
    }


    public static boolean Checkallballsmovement()// checks if all the ball stopped or not
    {
        if(!GameScreen.table.getCueball().Checkifballstopped()||!GameScreen.table.getBlackball().Checkifballstopped())
            return false;

        for(int i=0;i<7;i++)
        {
            if(!GameScreen.table.getSolidball()[i].Checkifballstopped())
                return false;

            if(!GameScreen.table.getStripeball()[i].Checkifballstopped())
                return false;
        }
        return true;
    }




}
