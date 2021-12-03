package PIUGame.States;
import PIUGame.GameObjects.UIImageButton;
import PIUGame.GameObjects.UIManager;
import PIUGame.Graphics.Assets;
import PIUGame.Input.ClickListener;
import PIUGame.Maps.Map;
import PIUGame.Maps.MapElements;
import PIUGame.RefLinks;
import PIUGame.Items.*;
import PIUGame.States.Difficulty.LevelDifficulty;
import PIUGame.States.PlayStateUpdates.ReinitializeObjects;

import java.util.List;
import java.awt.*;
import java.util.ArrayList;

public class PlayState extends State{


    private UIManager resumeManager;                                        // Referinta catre obiectul care gestioneaza meniul de Resume(cand se apasa pe butonul MENU)
    private RefLinks refLink;                                               // Referinta catre clasa ce are acces la celelalte obiecte

    private static Hero hero;                                               // < Referinta catre obiectul animat erou (controlat de utilizator).
    private Map map;                                                        // < Referinta catre harta curenta.

    private int index_level = 1;                                            // Indexul nivelului
    public static List<Monster> monster;                                    // Lista monstrilor
    public static Stone[] stone;                                            // Lista pietrelor pe care trebuie sa le colecteze eroul
    public static MapElements map_elements;                                 // Elementele de pe harta care sunt desenate peste tiles pe baza coordonatelor date
    public static LevelDifficulty levelDifficulty = LevelDifficulty.EASY;   // Nivelul de dificultate al jocului
    public static ReinitializeObjects reinitializeObjects;                  // La fiecare nivel o serie de obiecte trebuie reinitializate
    public List<Sword> swords = new ArrayList<Sword>();                        // Lista sabiilor pe care eroul le creeaza cand ataca, apasand tasta space
    public List<Explosion> explosions = new ArrayList<Explosion>();         // Lista animatiilor de explozii cand sabia intalneste un obstacol

    // Retine timpul de cand s-a inceput jocul
    public static int minutes = 0;
    public static int seconds = 0;
    public static int timer_count = 0;                                  // Tine evidenta update-urilor pentru a numara o secunda(Update-urile se realizeza de 60/s)



    // brief Constructorul de initializare al clasei
    // param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program
    public PlayState(RefLinks refLink)
    {
        ///Apel al constructorului clasei de baza
        super(refLink);
        this.refLink = refLink;

        // se actualizeaza obiectele cand starea de playState este resetata deoarece se pierde focusul(este esentiala la trecerea dintr-o stare in alta: de ex, din starea meniu in playState)
        updateObjectWithListener();

        ///Construieste harta jocului
        map = new Map(refLink, index_level);

        ///Referinta catre harta construita este setata si in obiectul shortcut pentru a fi accesibila si in alte clase ale programului.
        refLink.SetMap(map);

        ///Construieste eroul
        hero = new Hero(refLink,150, 200);


        // creez obiect care imi va gestiona obiectele in diferite niveluri
        reinitializeObjects = new ReinitializeObjects(refLink);
        reinitializeObjects.reorganizeObject(index_level);

        map_elements = new MapElements(refLink, index_level);
    }


    // se actualizeaza obiectele cand starea de playState este resetata deoarece se pierde focusul
    public void updateObjectWithListener(){
        // Seteaza un buton de resume
        resumeManager = new UIManager(refLink);
        refLink.GetMouseManager().setUIManager(resumeManager);

        resumeManager.addObject(new UIImageButton( 1100, 28, 130, 44, Assets.menu_button_image, new ClickListener() {
            @Override
            public void onClick() {
                refLink.GetMouseManager().setUIManager(null);
                State.SetState(new ResumeState(refLink));
            }
        }));
    }


    @Override
    public void Update()
    {
        if(!refLink.GetKeyManager().pause_value) {      //jocul este pornit
            map.Update();
            hero.Update();

            if(!swords.isEmpty()){
                for(int i = 0; i< swords.size(); i++){
                    swords.get(i).Update();
                    if(swords.isEmpty()){
                        break;
                    }
                }
            }

            if(!explosions.isEmpty()){
                for(Explosion e: explosions){
                    e.Update();
                    if(explosions.isEmpty()){
                        break;
                    }
                }
            }

            for(Stone s: stone) {
                s.Update();
            }

            if(!monster.isEmpty()) {
                for (Monster t : monster) {
                    t.Update();
                    if(monster.isEmpty()){
                        break;
                    }
                }
            }

            for (Monster m : monster) {
                if (m.hasKilledPlayer()) {
                    System.out.println("mort");

                    hero.createExplosionEffect();

                    hero.resetPosition();
                    hero.SetLife(hero.GetLife() - 1);
                    for (Monster t : monster) {
                        t.resetPosition();
                    }
                    if (hero.GetLife() == 0 ) {
                        //hero.setIsAlive(false);

                        //waitForExplosionEffect();
//                        if(explosion_effect_finished == true){
//                            State.SetState(new LoseState(refLink));
//                        }
                        State.SetState(new LoseState(refLink));
//                        refLink.GetGame().getDatabaseConnection().update();
                    }
                }
            }

            if(timer_count <= 60){
                timer_count++;
            }
            else{
                timer_count = 0;
                if(seconds == 59){
                    minutes++;
                    seconds = 0;
                }else{
                    seconds++;
                }
            }
        }

        if(hero.levelFinished()){
            if(index_level == 2){
//                refLink.GetGame().getDatabaseConnection().update();
                State.SetState(new FinishedGame(refLink));
            }else {
                index_level++;
                hero.resetPosition();
                //hero.resetStone();

                reinitializeObjects.reorganizeObject(index_level);

                for (Monster t : monster) {
                    t.resetPosition();
                }

                map = new Map(refLink, index_level);
                refLink.SetMap(map);

            }
        }
        //resumeManager.Update();
    }


    // brief Deseneaza (randeaza) pe ecran starea curenta a jocului.
    // param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
    @Override
    public void Draw(Graphics g)
    {


        if(!refLink.GetKeyManager().pause_value) {      // jocul este pornit

            //map_elements.Draw(g, index_level);

            map.Draw(g);
            map_elements.Draw(g, index_level);
            hero.Draw(g);

            if(!swords.isEmpty()){
                for(Sword s: swords){
                    s.Draw(g);
                }
            }


            if(!explosions.isEmpty()){
                for(Explosion e: explosions){
                    e.Draw(g);
                    if(!explosions.isEmpty()){
                        break;
                    }
                }
            }

            for(Stone s: stone) {
                s.Draw(g);
            }

            for (Monster t : monster) {
                t.Draw(g);
            }



        }else{
            map.Draw(g);
            map_elements.Draw(g, index_level);
            hero.Draw(g);

            for(Stone s: stone) {
                s.Draw(g);
            }

            for (Monster t : monster) {
                t.Draw(g);
            }

            // afiseaza pe centru mesajul "PAUSE" cand jocul este pus pe pauza(a fost apasata tasta "P")
            Font font_pause = new Font("arial", 1, 50);
            g.setFont(font_pause);
            g.setColor(Color.WHITE);
            g.drawString("PAUSE", (int)refLink.GetWidth()/2 - 50, (int)refLink.GetHeight()/2 - 50);
        }

        // afiseaza timpul
        g.setColor(Color.GRAY);
        g.fillRoundRect(1230, 30, 110, 40, 20, 20);

        Font font_timer = new Font("arial", 1, 25);
        g.setFont(font_timer);
        g.setColor(Color.WHITE);
        g.drawString(""+ minutes + " : " + seconds, 1250, 60);


        // deseneaza butonul de resume
        resumeManager.Draw(g);

    }

    public static List<Monster> GetMonster(){
        return monster;
    }

    public static void setMonster(List<Monster> monster) {
        PlayState.monster = monster;
    }

    public static Hero GetHero(){
        return hero;
    }

    public int getIndex_level(){
        return index_level;
    }

    public static LevelDifficulty getLevelDifficulty() {
        return levelDifficulty;
    }

    public static void setLevelDifficulty(LevelDifficulty levelDifficulty) {
        PlayState.levelDifficulty = levelDifficulty;
    }

    public static Stone[] getStone() {
        return stone;
    }

    public static void setStone(Stone[] stone) {
        PlayState.stone = stone;
    }

    public List<Sword> getSwords() {
        return swords;
    }

    public void setSwords(List<Sword> swords) {
        this.swords = swords;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public void setExplosions(List<Explosion> explosions) {
        this.explosions = explosions;
    }
}
