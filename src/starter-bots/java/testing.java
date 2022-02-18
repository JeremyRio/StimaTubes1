package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {
    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();
    private int score;

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private final static Command FIX = new FixCommand();

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();


    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        directionList.add(-1);
        directionList.add(1);

    }

    public Command run(){

        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
        List<Object> blocks2 = getBlocksInFront2(myCar.position.lane, myCar.position.block);

        if (myCar.damage >= 1) {
            return new FixCommand();
        }

        if (blocks2.contains(Terrain.MUD) || blocks2.contains(Terrain.WALL) || blocks2.contains(Terrain.OIL_SPILL)){
            if(myCar.position.lane == 1){
                int tengah = getScoreInFront2(myCar.position.lane,myCar.position.block);
                int kanan = getScoreInFront(myCar.position.lane+1,myCar.position.block);
                if (tengah > 0 && kanan > 0){
                    if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                        return LIZARD;
                    }
                }

                if (tengah < kanan){
                    return ACCELERATE;
                } else {
                    return TURN_RIGHT;
                }
            }
            if(myCar.position.lane == 4){
                int kiri = getScoreInFront(myCar.position.lane-1,myCar.position.block);
                int tengah = getScoreInFront2(myCar.position.lane,myCar.position.block);
                if (tengah > 0 && kiri > 0){
                    if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                        return LIZARD;
                    }
                }

                if (tengah < kiri){
                    return ACCELERATE;
                } else {
                    return TURN_LEFT;
                }
            }
            if (myCar.position.lane == 2){
                int kiri = getScoreInFront(myCar.position.lane-1,myCar.position.block);
                int tengah = getScoreInFront2(myCar.position.lane,myCar.position.block);
                int kanan = getScoreInFront(myCar.position.lane+1,myCar.position.block);
                if (tengah > 0 && kanan > 0 && kiri > 0){
                    if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                        return LIZARD;
                    }
                }

                if(tengah<=kanan && tengah<=kiri){
                    return ACCELERATE;
                }

                if(kanan<=kiri && kanan<=tengah){
                    return TURN_RIGHT;
                }

                if(kiri<=kanan && kiri<=tengah) {
                    return TURN_LEFT;
                }
            }

            if (myCar.position.lane == 3){
                int kiri = getScoreInFront(myCar.position.lane-1,myCar.position.block);
                int tengah = getScoreInFront2(myCar.position.lane,myCar.position.block);
                int kanan = getScoreInFront(myCar.position.lane+1,myCar.position.block);
                if (tengah > 0 && kanan > 0 && kiri > 0){
                    if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                        return LIZARD;
                    }
                }

                if(tengah<=kanan && tengah<=kiri){
                    return ACCELERATE;
                }

                if(kiri<=kanan && kiri<=tengah){
                    return TURN_LEFT;
                }

                if(kanan<=kiri && kanan<=tengah){
                    return TURN_RIGHT;
                }

            }
        }

        if (myCar.speed <= 3){
            if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
                if (getScoreInFrontBoost(myCar.position.lane,myCar.position.block) == 0) {
                    return BOOST;
                }
            }
            else {
                return ACCELERATE;
            }
        }

        if (myCar.speed <= 9){
            if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
                if (getScoreInFrontBoost(myCar.position.lane,myCar.position.block) == 0) {
                    return BOOST;
                }
            }
        }

        if(opponent.position.block>myCar.position.block){
            int simpanmusuh = opponent.position.lane;
            int simpandiri = myCar.position.lane;
            if(simpanmusuh-simpandiri <= 1 && simpanmusuh-simpandiri >= -1){
                if(hasPowerUp(PowerUps.EMP, myCar.powerups)) {
                    return EMP;
                }
            }
        }


        if (hasPowerUp(PowerUps.TWEET, myCar.powerups)){
            return new TweetCommand(opponent.position.lane,opponent.position.block+16);
        }

        if(opponent.position.block<myCar.position.block){
            if(hasPowerUp(PowerUps.OIL, myCar.powerups)) {
                return OIL;
            }
        }

        return ACCELERATE;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i < block - startBlock + myCar.speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    private List<Object> getBlocksInFront2(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + myCar.speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    private int getScoreInFront(int lane, int block) {
        score = 0;
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;


        Lane[] laneList = map.get(lane - 1);

        for (int i = max(block - startBlock, 0); i <= block - startBlock + myCar.speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            if (laneList[i].terrain == Terrain.MUD) {
                score += 1;
            }
            if (laneList[i].terrain == Terrain.WALL) {
                score += 2;
            }
            if (laneList[i].terrain == Terrain.OIL_SPILL) {
                score += 1;
            }

        }
        return score;
    }

    private int getScoreInFront2(int lane, int block) {
        score = 0;
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;
        int[] speed_note = {0,3,5,6,8,9};
        int note = 6;

        for (int k = 0; k < speed_note.length; k++){
            if (myCar.speed < speed_note[k]){
                note = k-1;
                k = speed_note.length;
            }
        }

        for (int j = 0; j < speed_note.length; j++){
            if (speed_note[j] == myCar.speed){
                note = j;
            }
        }

        Lane[] laneList = map.get(lane - 1);

        if (note < 5) {
            for (int i = max(block - startBlock, 0); i <= block - startBlock + speed_note[note + 1]; i++) {
                if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                    break;
                }

                if (laneList[i].terrain == Terrain.MUD) {
                    score += 1;
                }
                if (laneList[i].terrain == Terrain.WALL) {
                    score += 2;
                }
                if (laneList[i].terrain == Terrain.OIL_SPILL) {
                    score += 1;
                }

            }
        }

        else if (note == 5) {
            for (int i = max(block - startBlock, 0); i <= block - startBlock + maxSpeed; i++) {
                if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                    break;
                }

                if (laneList[i].terrain == Terrain.MUD) {
                    score += 1;
                }
                if (laneList[i].terrain == Terrain.WALL) {
                    score += 2;
                }
                if (laneList[i].terrain == Terrain.OIL_SPILL) {
                    score += 1;
                }

            }
        }

        else if (note > 5) {
            for (int i = max(block - startBlock, 0); i <= block - startBlock + 15; i++) {
                if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                    break;
                }

                if (laneList[i].terrain == Terrain.MUD) {
                    score += 1;
                }
                if (laneList[i].terrain == Terrain.WALL) {
                    score += 2;
                }
                if (laneList[i].terrain == Terrain.OIL_SPILL) {
                    score += 1;
                }

            }
        }
        return score;
    }

    private int getScoreInFrontBoost(int lane, int block) {
        score = 0;
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;


        Lane[] laneList = map.get(lane - 1);

        for (int i = max(block - startBlock, 0); i <= block - startBlock + 15; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            if (laneList[i].terrain == Terrain.MUD) {
                score += 1;
            }
            if (laneList[i].terrain == Terrain.WALL) {
                score += 2;
            }
            if (laneList[i].terrain == Terrain.OIL_SPILL) {
                score += 1;
            }

        }
        return score;
    }
}
