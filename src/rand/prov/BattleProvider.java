package rand.prov;

import java.util.Random;
import rand.ByteStream;

public class BattleProvider extends DataProvider {
    @Override
    protected void randomizeData(Random rng, int position, DataEntry data) {
        byte[] obj = data.getBytes();
        int objType = obj[0];
        int objPos = obj[1];
        int objValue = (obj[2] & 0xFF) | ((obj[3] & 0xFF) << 8);
        
        // If object is an enemy, randomize it.
        if (objType == 0x11 && objValue >= 1) {
            // Only randomize regular viruses; not bosses.
            if (objValue <= 0xAE) {
                int enemyFamily = (objValue - 1) / 6;
                int enemyRank = (objValue - 1) % 6;
                
                // Do not randomize Rare viruses.
                if (enemyRank <= 3) {
                    // Choose a random family but keep the same rank.
                    enemyFamily = rng.nextInt(29);
                }
                
                objValue = enemyFamily * 6 + enemyRank + 1;
            }
        }
        
        obj[0] = (byte)(objType & 0xFF);
        obj[1] = (byte)(objPos & 0xFF);
        obj[2] = (byte)(objValue & 0xFF);
        obj[3] = (byte)((objValue >> 8) & 0xFF);
        data.setBytes(obj);
    }

    @Override
    public void execute(ByteStream stream) {
        // Get to the enemy layout.
        stream.advance(12);
        int layoutPtr = stream.readInt32();
        stream.push();
        stream.setPosition(layoutPtr);
        
        // Read all enemy objects.
        int objType, objPos, objValue;
        while ((objType = stream.readUInt8()) < 0xF0) {
            objPos = stream.readInt8();
            objValue = stream.readInt16();
            
            // If the object is an enemy, register it.
            if (objType == 0x11 && objValue >= 1) {
                stream.advance(-4);
                registerData(stream, 4);
            }
        }
        
        stream.pop();
    }
}
