package PIUGame.Tiles;

import PIUGame.Graphics.Assets;

public class SolidGrassTile extends Tile{

    public SolidGrassTile(int id)
    {
        /// Apel al constructorului clasei de baza
        super(Assets.grass, id);
    }
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
