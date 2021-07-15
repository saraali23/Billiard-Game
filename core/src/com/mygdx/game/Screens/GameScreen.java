package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Screens.BallInfo.*;
import com.mygdx.game.SpaceGame;
import jdk.nashorn.internal.objects.Global;

public class GameScreen implements Screen {

    public static World world;
    public static Box2DDebugRenderer debugRenderer;
    public static OrthographicCamera camera;
    SpaceGame spaceGame;
    // makes the world move by the time (final)

    public static CueBall cueball; // white ball
    public static BlackBall blackball;
    public static SolidBall solidball[]= new SolidBall[7];
    public static StripeBall stripeball[]= new StripeBall[7];
    public Stick stick;
    public Edge edge1 ;
    public Edge edge2 ;
    public Edge edge3 ;
    public Edge edge4 ;
    public Edge edge5 ;
    public Edge edge6 ;

    public Player player1;
    public Player player2;

    //drawing
    public Array<Body> tmpBodies;
    public Sprite table;

    public int counter;
    public Texture tex;
    public TextureRegion texreg;


    public GameScreen(SpaceGame spaceGame) {
       this.spaceGame = spaceGame;
   }
    @Override
    public void show() {

        table=new Sprite(new Texture("Assets/table.png"));
        table.setPosition(-SpaceGame.VirtualWidth/40,-SpaceGame.VirtualHeight/40);
        table.setSize(SpaceGame.VirtualWidth/20,SpaceGame.VirtualHeight/20);
        world = new World(new Vector2(0,0),true); // x= 0 and y =0 means no gravity
        debugRenderer = new Box2DDebugRenderer();
        camera =new OrthographicCamera(spaceGame.VirtualWidth/20,spaceGame.VirtualHeight/20);  // zooms in 20 times from the fullscreen

        // initialize the objects and sets the initial position
        cueball = new CueBall(	new Vector2(-15,0),	new Sprite(new Texture("Assets/whiteBall.png")));
        stick=new Stick(cueball,new Sprite(new Texture("Assets/Stick.png")));
        edge1 =  new Edge(new Vector2(-1,-9.6f),new Vector2(0.02f,-11f ),new Vector2(-1,9.6f),new Vector2(0.02f,11f),new Vector2(24.5f,0));
        edge2 =  new Edge(new Vector2(1,-9.6f),new Vector2(0.02f,-11f ),new Vector2(1,9.6f),new Vector2(0.02f,11f),new Vector2(-24.5f,0));
        edge3 =  new Edge(new Vector2(-10.5f,1.1f),new Vector2(-11,0.02f ),new Vector2(9.8f,1.1f),new Vector2(11,0.02f),new Vector2(11.8f,-12.65f));
        edge4 =  new Edge(new Vector2(-9.8f,1.1f),new Vector2(-11,0.02f ),new Vector2(10.3f,1.1f),new Vector2(10.8f,0.02f),new Vector2(-12.3f,-12.65f));
        edge5 =  new Edge(new Vector2(-10.5f,-1.1f),new Vector2(-11,0.02f ),new Vector2(9.8f,-1.1f),new Vector2(11,0.02f),new Vector2(11.8f,12.65f));
        edge6 =  new Edge(new Vector2(-9.8f,-1.1f),new Vector2(-11,0.02f ),new Vector2(10.3f,-1.1f),new Vector2(10.8f,0.02f),new Vector2(-12.3f,12.65f));




        tmpBodies= new Array<Body>();
        int count=0; // used as index for the balls

        int num=1;
        // a function to set the balls position
        for (int i=0;i<5;i++)
        {
            float x= (float)(i * (Math.sqrt(5) * 0.55))+8; // gets the x position

            for(float y : rowXs(i)) { //gets the y position ( loops on every item in royXs(i) )

                if(count<7)
                {
                    solidball[count++] = new SolidBall( new Vector2(x, y),new Sprite(new Texture(String.format("Assets/ball %d.png",num))));
                    num++;

                }

                else if(count==7) {

                    blackball = new BlackBall(new Vector2(x, y),new Sprite(new Texture("Assets/ball 8.png")));
                    count++;
                    num++;

                }
                else
                {
                    stripeball[count++%8]= new StripeBall(new Vector2(x, y),new Sprite(new Texture(String.format("Assets/ball %d.png",num))));
                    num++;
                }



            }

        }
        tex =new Texture(Gdx.files.internal("Assets/isolated-videogame-bar-vector-12737679.jpg"));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texreg=new TextureRegion(tex,0,0,981,138);


    }






    boolean check=true;
    boolean ch=false;
    boolean checkcueball=false;
    int disx,disy;
    int count=0;
    Vector3 vv;
    boolean touched =false;
    Vector3 ray=new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
    ShapeRenderer sr = new ShapeRenderer();
    ShapeRenderer rayy=new ShapeRenderer();
    @Override
    public void render(float delta) {

    counter= (int) Math.sqrt((Gdx.input.getX()-disx)*(Gdx.input.getX()-disx) + (Gdx.input.getY()-disy)*(Gdx.input.getY()-disy));
        System.out.println(cueball.CheckBallMovement());
    if(checkcueball)
    {

            vv = new Vector3((Gdx.input.getX()), Gdx.input.getY(), 0);//get mouse position

        GameScreen.camera.unproject(vv);//translate this position to our world coordinate
        if(vv.x>0)
            vv.x=Math.min(vv.x,23);
        else
            vv.x=Math.max(vv.x,-23);

        if(vv.y>0)
            vv.y=Math.min(vv.y,11);
        else
            vv.y=Math.max(vv.y,-11);


        cueball.getBall().setTransform(vv.x,vv.y,0);

        if(Gdx.input.isTouched()) {
            cueball.getBall().setActive(true);

            checkcueball=false;
        }
    check=true;
    }


      else  if(cueball.CheckBallMovement()) {//if cueball stopped moving
            if(check) stick.updateStickRotation();

            Gdx.input.setInputProcessor(new InputHandle() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if(cueball.CheckBallMovement()) {
                        System.out.println("tt");
                        check = false;
                        ch = true;
                        count = 10;
                        disx = Gdx.input.getX();
                        disy = Gdx.input.getY();
                    }
                    return super.touchDown(screenX, screenY, pointer, button);


                }




                @Override
                public boolean touchDragged ( int screenX, int screenY, int pointer){
                    if(cueball.CheckBallMovement()) {
                        if (ch) {
                            count++;
                            stick.updateStickRotation();
                            if (count >= 400)
                                count = 0;
                            check = false;
                            touched = true;
                            ray.set((Gdx.input.getX()), Gdx.input.getY(), 0);//get mouse position
                            GameScreen.camera.unproject(ray);//translate this position to our world coordinate
                            /*float adj = ray.x-cueball.getBall().getWorldCenter().x ;//line between mouse point x and stick center in x-axis
                            float opp =ray.y- cueball.getBall().getWorldCenter().y ;//line between mouse point y and stick center in Y-axis
                            float angle = (float) Math.atan2(opp, adj);//ge

                             */
                            sr.setColor(Color.BLACK);
                            sr.setProjectionMatrix(camera.combined);
                            sr.begin(ShapeRenderer.ShapeType.Line);
                            if(Math.sqrt(Math.pow(cueball.getBall().getWorldCenter().x-ray.x,2)+Math.pow(cueball.getBall().getWorldCenter().y-ray.y,2))>20){
                                float adj =ray.x-cueball.getBall().getWorldCenter().x ;//line between mouse point x and stick center in x-axis
                                float opp =ray.y- cueball.getBall().getWorldCenter().y ;//line between mouse point y and stick center in Y-axis
                                float angle = (float) Math.atan2(opp, adj);
                                sr.line(cueball.getBall().getWorldCenter().x,cueball.getBall().getWorldCenter().y,cueball.getBall().getPosition().x-(float)(Math.cos(angle)*20.0f),cueball.getBall().getPosition().y-(float)(Math.sin(angle)*20.0f));
                            }
                            sr.end();
                        }
                    }
                    return super.touchDragged(screenX, screenY, pointer);

                }

                @Override
                public boolean touchUp ( int screenX, int screenY, int pointer, int button){
                    if(cueball.CheckBallMovement()) {
                        if (ch) {
                            if (cueball.CheckBallMovement())
                                if (count > 400)
                                    count = 400;
                            cueball.getBall().applyForceToCenter(new Vector2((float) (Math.cos(stick.angle) * 100 * count), (float) (Math.sin(stick.angle) * 100 * count)), true);
                            check = true;
                        }
                        // counter=5;
                        count = 0;
                        ch = false;
                        touched = false;
                    }
                    return super.touchUp(screenX, screenY, pointer, button);
                }



            });

            //if(Gdx.input.isTouched())
                //counter+=2;
           if(touched) {
               count++;
               counter+=3;
           }

        }


        else {
        stick.getStick().setTransform(10, 30, 0);
        if(Gdx.input.isTouched()){
            stick.getStick().setTransform(10, 30, 0);
        }

        }



        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        world.step(1f/60f, 5, 8); // to make the world feel the time

       spaceGame.batch.setProjectionMatrix(camera.combined);
       spaceGame. batch.begin();
       table.draw(spaceGame.batch);
        world.getBodies(tmpBodies);
        Sprite sprites=new Sprite(texreg);
        sprites.flip(true,false);
        sprites.setSize(11.3f,5f  * sprites.getHeight() / sprites.getWidth());
        sprites.setPosition(-14, 14);
        sprites.draw(spaceGame.batch);

        for(Body body:tmpBodies)
        {

        if((body.getPosition().y>=11.7f||body.getPosition().y<=-11.7f)&&body.getLinearDamping()!=0) {

            if(body.equals(cueball.getBall()))
            {
                System.out.println("meow22");
                body.setTransform(0,0,0);
                cueball.getBall().setLinearVelocity(0,0);
                cueball.getBall().setAngularVelocity(0);
                cueball.getFixturedef().isSensor=true;
                cueball.getBall().setActive(false);
                body.setTransform(0,0,0);

                checkcueball=true;
            }
            else
            body.setTransform(100, 100, 0);
        }

        }


        for(Body body:tmpBodies)
        {
            if(body.getUserData()!=null && body.getUserData() instanceof Sprite)
            {
                Sprite sprite=(Sprite)body.getUserData();
                sprite.setPosition(body.getPosition().x-sprite.getWidth()/2,body.getPosition().y-sprite.getHeight()/2);
                sprite.setRotation(body.getAngle()* MathUtils.radiansToDegrees);
                sprite.draw(spaceGame.batch);
            }
        }


        if(count<=100) {
            sr.setColor(Color.GREEN);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setProjectionMatrix(camera.combined);
            Vector3 ve = new Vector3(30, 400, 0);
            GameScreen.camera.unproject(ve);
            sr.rect(ve.x,ve.y,0.5f,count/50f);

            sr.end();
        }
        else if(count>100&&count<=200){
            sr.setColor(Color.YELLOW);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            Vector3 ve = new Vector3(30, 400, 0);
            GameScreen.camera.unproject(ve);
            sr.rect(ve.x,ve.y,0.5f,count/50f);
            sr.end();

        }
        else{
            sr.setColor(Color.RED);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            Vector3 ve = new Vector3(30, 400, 0);
            GameScreen.camera.unproject(ve);
            sr.rect(ve.x,ve.y,0.5f,count/50f);

            sr.end();


        }
         if(touched) {
           rayy.setProjectionMatrix(camera.combined);
           rayy.begin(ShapeRenderer.ShapeType.Line);
           rayy.setColor(Color.WHITE);
           ray.set(Gdx.input.getX(), Gdx.input.getY(), 0);
           camera.unproject(ray);
           if(Math.sqrt(Math.pow(cueball.getBall().getWorldCenter().x-ray.x,2)+Math.pow(cueball.getBall().getWorldCenter().y-ray.y,2))>5||Math.sqrt(Math.pow(cueball.getBall().getWorldCenter().x-ray.x,2)+Math.pow(cueball.getBall().getWorldCenter().y-ray.y,2))<5){
                 float adj =ray.x-cueball.getBall().getWorldCenter().x ;//line between mouse point x and stick center in x-axis
                 float opp =ray.y- cueball.getBall().getWorldCenter().y ;//line between mouse point y and stick center in Y-axis
                 float angle = (float) Math.atan2(opp, adj);
               rayy.line(cueball.getBall().getWorldCenter().x,cueball.getBall().getWorldCenter().y,cueball.getBall().getPosition().x-(float)(Math.cos(angle)*-10.0f),cueball.getBall().getPosition().y-(float)(Math.sin(angle)*-10.0f));
           }
           rayy.end();
        }




       spaceGame.batch.end();
        //debugRenderer.render(world,camera.combined);  // to render the bodies






    }
    public static float[] rowXs(int rowNumber) {
        float R = 0.75f; // radius of the ball
        switch (rowNumber) {
            case 0: return new float[] {0};
            case 1: return new float[] {-R, R};
            case 2: return new float[] {-2*R, 0, 2*R};
            case 3: return new float[] {-3*R, -R, R, 3*R};
            case 4: return new float[] {-4*R, -2*R, 0, 2*R, 4*R};

            default: throw new IllegalArgumentException("no more than 5 rows");
        }
    }




    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
      spaceGame.batch.dispose();
      sr.dispose();
      rayy.dispose();
      texreg.getTexture().dispose();
    }

}
