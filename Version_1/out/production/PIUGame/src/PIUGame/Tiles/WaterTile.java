package PIUGame.Tiles;

import PIUGame.Graphics.Assets;


 public class WaterTile extends Tile
{

       // param id Id-ul dalei util in desenarea hartii.
    public WaterTile(int id)
    {
        super(Assets.water, id);
    }

       // brief Suprascrie metoda IsSolid() din clasa de baza in sensul ca va fi luat in calcul in caz de coliziune.
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
