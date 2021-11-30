package PIUGame.Items;

import java.awt.*;
import java.awt.image.BufferedImage;

import PIUGame.Graphics.Animation;
import PIUGame.RefLinks;
import PIUGame.Graphics.Assets;
import PIUGame.States.PlayState;

import static java.lang.Thread.sleep;

/*
    brief Implementeaza notiunea de erou/player (caracterul controlat de jucator).
    Elementele suplimentare pe care le aduce fata de clasa de baza sunt:
        imaginea (acest atribut poate fi ridicat si in clasa de baza)
        deplasarea
        atacul (nu este implementat momentan)
        dreptunghiul de coliziune
 */
public class Hero extends Character
{
    //Animation
    private Animation animDown;     //mod_3
    private Animation animUp;     //mod_3
    private Animation animLeft;     //mod_3
    private Animation animRight;     //mod_3
    private Animation noAnim;     //mod_3

    private BufferedImage lifeImage;
    private BufferedImage stoneImage;

    private int finish_zone_x = 0;
    private int finish_zone_y = 0;

    private float target_to_follow_x;
    private float target_to_follow_y;
    private boolean  is_arrive_at_gate = false;
    private boolean in_finish_zone = false;


    private BufferedImage image;    /*!< Referinta catre imaginea curenta a eroului.*/

    public int nr_stone = 0;

    /*
        \brief Constructorul de initializare al clasei Hero.
        \param refLink Referinta catre obiectul shortcut (obiect ce retine o serie de referinte din program).
        \param x Pozitia initiala pe axa X a eroului.
        \param y Pozitia initiala pe axa Y a eroului.
     */
    public Hero(RefLinks refLink, float x, float y)
    {
        ///Apel al constructorului clasei de baza
        super(refLink, x,y, Character.DEFAULT_CREATURE_WIDTH, Character.DEFAULT_CREATURE_HEIGHT);

        ///Seteaza imaginea de start a eroului
        //image = Assets.heroLeft;

        ///Stabilieste pozitia relativa si dimensiunea dreptunghiului de coliziune, starea implicita(normala)
        normalBounds.x = 20;
        normalBounds.y = 20;
        normalBounds.width = 26;
        normalBounds.height = 36;

        ///Stabilieste pozitia relativa si dimensiunea dreptunghiului de coliziune, starea de atac
        attackBounds.x = 10;
        attackBounds.y = 10;
        attackBounds.width = 38;
        attackBounds.height = 38;


        //Animation
        animDown = new Animation(100, Assets.hero_down);      //mod_3
        animUp = new Animation(100, Assets.hero_up);      //mod_3
        animLeft = new Animation(100, Assets.hero_left);      //mod_3
        animRight = new Animation(100, Assets.hero_right);      //mod_3
        noAnim = new Animation(0, Assets.hero_noAnimation);

        lifeImage = Assets.life_image;
        stoneImage = Assets.stone_image;
    }

        // brief Actualizeaza pozitia si imaginea eroului.
    @Override
    public void Update()
    {
        //Animation
        animDown.Update();
        animUp.Update();
        animLeft.Update();
        animRight.Update();
        noAnim.Update();

        //System.out.println(nr_stone);



        for(Stone s: PlayState.stone){
           if(s.stone_collected && !s.visited){
               nr_stone++;
               s.visited = true;
               //s.stone_collected = true;
               //System.out.println(s.stone_collected);
               break;

           }
        }

        ///Verifica daca a fost apasata o tasta
        GetInput();
        ///Actualizeaza pozitia
        Move();
        ///Actualizeaza imaginea

        refLink.getGameCamera().centerOnEntity(this);       //mod_2

    }

        // brief Verifica daca a fost apasata o tasta din cele stabilite pentru controlul eroului.
    private void GetInput()
    {
        ///Implicit eroul nu trebuie sa se deplaseze daca nu este apasata o tasta
        xMove = 0;
        yMove = 0;
        ///Verificare apasare tasta "sus"

        if(in_finish_zone == true && stonesAreCollected()) {             // player-ul se afla in zona de trecere catre urmatorul nivel
            if(x > target_to_follow_x - 10 && y < target_to_follow_y + 10){
                is_arrive_at_gate = true;
            }else{
                if(x < target_to_follow_x){
                    xMove = speed / 8;
                }
                else{
                    xMove = -speed / 8;
                }
                if(y > target_to_follow_y){
                    yMove = -speed / 8;
                }
                else{
                    yMove = speed / 8;
                }
            }
        }
        else{                               // player-ul se deplaseaza normal
            if(refLink.GetKeyManager().up)
            {
                yMove = -speed;
            }
            ///Verificare apasare tasta "jos"
            if(refLink.GetKeyManager().down)
            {
                yMove = speed;
            }
            ///Verificare apasare tasta "left"
            if(refLink.GetKeyManager().left)
            {
                xMove = -speed;
            }
            ///Verificare apasare tasta "dreapta"
            if(refLink.GetKeyManager().right)
            {
                xMove = speed;
            }
        }

        //System.out.println("x= "+ x + "    y= " + y);
    }

        // brief Randeaza/deseneaza eroul in noua pozitie.
        // brief g Contextul grafi in care trebuie efectuata desenarea eroului.
    @Override
    public void Draw(Graphics g)
    {
        // g.drawImage(getCurrentAnimationFrame(), (int)(x - refLink.getGameCamera().getxOffset()), (int)(y - refLink.getGameCamera().getyOffset()), width, height, null);

        //g.fillRect(20, 20, 200, 60);
        //g.fillRect();
        //g.setColor(Color.black);


        // ---------------------------------------------------------------
        for(int i=0; i < life; i++){
            g.drawImage(lifeImage, 30 + i * 50, 30, 40, 40, null);
        }
        //g.setColor(Color.black);
        //g.fillRect((int)(refLink.GetWidth())-200,(int)(refLink.GetHeight())-200, 200, 200);

        g.drawImage(stoneImage, 200, 30, 40, 40, null);
        Font font1 = new Font("arial", 1, 40);
        g.setFont(font1);
        g.setColor(Color.white);

        String nr = Integer.toString(nr_stone);
        g.drawString(nr, 240, 50);


        // ---------------------------------------------------------------



        ///doar pentru debug daca se doreste vizualizarea dreptunghiului de coliziune altfel se vor comenta urmatoarele doua linii
        g.setColor(Color.blue);
//        g.fillRect((int)(x + bounds.x), (int)(y + bounds.y), bounds.width, bounds.height);
//        g.fillRect((int)(x + bounds.x - refLink.getGameCamera().getxOffset()), (int)(y + bounds.y - refLink.getGameCamera().getyOffset()), bounds.width, bounds.height);

//        g.fillRect((int)(x - refLink.getGameCamera().getxOffset()), (int)(y - refLink.getGameCamera().getyOffset()), width, height);

        System.out.println("player_x: " + x + " ---  player_y: " + y);

        g.drawImage(getCurrentAnimationFrame(), (int)(x - refLink.getGameCamera().getxOffset()), (int)(y - refLink.getGameCamera().getyOffset()), width, height, null);
    }

    private BufferedImage getCurrentAnimationFrame(){
        if(xMove < 0){      //moving to the left
            return animLeft.getCurrentFrame();
        }else if(xMove > 0){        //moving to the right
            return animRight.getCurrentFrame();
        }else if(yMove < 0){        //moving up
            return animUp.getCurrentFrame();
        }else if(yMove > 0){        //moving down
            return animDown.getCurrentFrame();
        }
        return noAnim.getCurrentFrame();
    }

    public boolean levelFinished(){
        if(playerInFinishZone() && stonesAreCollected() && is_arrive_at_gate){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean playerInFinishZone(){
        //if(x > refLink.GetWidth() + refLink.getGameCamera().getxOffset()-200 && y > refLink.GetHeight() +refLink.getGameCamera().getyOffset() -200){
        if(x > finish_zone_x && y > finish_zone_y){
            in_finish_zone = true;
            return true;
        }
        else{
            if(x < finish_zone_x-80 && y < finish_zone_y - 80) {
                in_finish_zone = false;
            }
            return false;
        }
    }

    public boolean stonesAreCollected(){

        if(nr_stone >= 1){
            //System.out.println("collected-->>>");
            return true;
        }
        else{
            //System.out.println("NOTT   collected");
            return false;
        }
    }

    public void resetStone(){
        for(Stone s: PlayState.stone){
            s.resetStoneStatus();
        }
        nr_stone =0;
    }

    public int getNr_stone(){
        return nr_stone;
    }

    public int getFinish_zone_x() {
        return finish_zone_x;
    }

    public void setFinish_zone_x(int finish_zone_x) {
        this.finish_zone_x = finish_zone_x;
    }

    public int getFinish_zone_y() {
        return finish_zone_y;
    }

    public void setFinish_zone_y(int finish_zone_y) {
        this.finish_zone_y = finish_zone_y;
    }

    public float getTarget_to_follow_x() {
        return target_to_follow_x;
    }

    public void setTarget_to_follow_x(float target_to_follow_x) {
        this.target_to_follow_x = target_to_follow_x;
    }

    public float getTarget_to_follow_y() {
        return target_to_follow_y;
    }

    public void setTarget_to_follow_y(float target_to_follow_y) {
        this.target_to_follow_y = target_to_follow_y;
    }
}


