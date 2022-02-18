package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.State;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {
    private List<Command> directionList = new ArrayList<>();

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
    private final static Command NOTHING = new DoNothingCommand();

    public Bot(Random random, GameState gameState) {
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        directionList.add(TURN_RIGHT);
        directionList.add(TURN_LEFT);
    }

    public Command run() {

        int middleScore;
        int lizardScore;
        int leftScore = -200;
        int rightScore = -200;
        List<Object> accelerateBlocks;
        List<Object> boostBlocks;
        List<Object> blocks;
        int[] speedList = { 3, 0, 0, 5, 0, 6, 8, 0, 9 };

        /**
         * Mencoba untuk melakukan perintah FIX
         * ketika mobil memiliki damage ≥ 2
         */

        if (myCar.damage >= 2) {
            return FIX;
        }

        /**
         * Menyimpan block di jalur tengah depan mobil sejumlah nilai yang sama dengan
         * kecepatan mobil selanjutnya (speedtList[myCar.speed]).
         * Jika kecepatan mobil ≥ 9, blok yang diambil sebesar kecepatan mobil itu juga
         */

        if (myCar.speed < 9) {
            accelerateBlocks = getBlocksInFront(myCar.position.lane, myCar.position.block + 1,
                    speedList[myCar.speed] - 1);
        } else {
            accelerateBlocks = getBlocksInFront(myCar.position.lane, myCar.position.block + 1, myCar.speed - 1);
        }

        /**
         * Menyimpan block di jalur tengah depan mobil
         * sejumlah nilai yang sama dengan
         * kecepatan mobil sekarang (myCar.speed)
         */
        blocks = getBlocksInFront(myCar.position.lane, myCar.position.block + 1, myCar.speed - 1);

        /**
         * Mengecek apakah terdapat rintangan dalam list blocks
         */
        if (isNotClear(blocks)) {
            /**
             * Mengambil skor dari accelerateBlock (jalur tengah next speed), block
             * (jalur tengah speed sekarang), jalur kiri, serta jalur kanan.
             * Jika bot berada di pojok kiri/kanan maka skor
             * jalur kiri/kanan tidak perlu dihitung
             */
            middleScore = getScore(accelerateBlocks);
            lizardScore = getScore(blocks);
            if (myCar.position.lane != 1) {
                leftScore = getScore(getBlocksInFront(myCar.position.lane - 1, myCar.position.block,
                        myCar.speed - 1));
            }
            if (myCar.position.lane != 4) {
                rightScore = getScore(getBlocksInFront(myCar.position.lane + 1,
                        myCar.position.block, myCar.speed - 1));
            }

            /**
             * Jika jalur kiri, jalur kanan, serta jalur tengah speed sekarang
             * memiliki skor negatif (terdapat rintangan) dan bot memiliki PowerUp
             * Lizard, bot eksekusi perintah USE_LIZARD
             */
            if (rightScore < 0 && lizardScore < 0 && leftScore < 0 && hasPowerUp(PowerUps.LIZARD)) {
                return LIZARD;
            }

            /**
             * Eksekusi perintah gerakan menuju jalur kiri,
             * jalur kanan, atau jalur tengah next speed yang memiliki skor tertinggi
             */
            else if (middleScore >= leftScore && middleScore >= rightScore) {
                return ACCELERATE;
            } else if (leftScore >= middleScore && leftScore >= rightScore
                    && myCar.position.lane != 1) {
                return TURN_LEFT;
            } else if (rightScore >= middleScore && rightScore >= leftScore
                    && myCar.position.lane != 4) {
                return TURN_RIGHT;
            }
        }

        /**
         * Mengecek jika musuh ada di depan pemain dan berada di baris dekat pemain.
         * Jika musuh ditemukan dan memiliki powerup EMP, maka EMP digunakan
         */
        if ((opponent.position.lane == myCar.position.lane
                || opponent.position.lane == myCar.position.lane - 1
                || opponent.position.lane == myCar.position.lane + 1)
                && opponent.position.block > myCar.position.block) {
            if (hasPowerUp(PowerUps.EMP)) {
                return EMP;
            }
        }

        /** Mencoba menggunakan PowerUp TWEET sesuai dengan kondisi */
        if (hasPowerUp(PowerUps.TWEET) && myCar.state != State.USED_TWEET) {
            return new TweetCommand(opponent.position.lane,
                    opponent.position.block + opponent.speed + 2);
        }

        /** Mencoba menggunakan PowerUp OIL sesuai dengan kondisi */
        if (opponent.position.lane == myCar.position.lane
                && opponent.position.block + 5 < myCar.position.block) {
            if (hasPowerUp(PowerUps.OIL)) {
                return OIL;
            }
        }

        /** Menyimpan block jalur tengah sebanyak 15 block di depan mobil */
        boostBlocks = getBlocksInFront(myCar.position.lane, myCar.position.block + 1, 14);

        /**
         * Mencoba mengeksekusi PowerUp BOOST. Jika mobil memiliki damage, mobil
         * diperbaiki terlebih dahulu
         */
        if (hasPowerUp(PowerUps.BOOST) && myCar.speed < 15 && !isNotClear(boostBlocks)) {
            if (myCar.damage >= 1) {
                return FIX;
            } else {
                return BOOST;
            }
        } else {
            /**
             * Mobil tidak melakukan apa-apa (NOTHING) jika
             * perintah ACCELERATE mengakibatkan mobil menabrak rintangan
             */
            if (isNotClear(accelerateBlocks)) {
                return NOTHING;
            } else {
                return ACCELERATE;
            }
        }
    }

    /**
     * Mengecek apakah terdapat suatu power up yang
     * tersedia dalam bot myCar
     */
    private Boolean hasPowerUp(PowerUps powerUpToCheck) {
        for (PowerUps powerUp : myCar.powerups) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mengembalikan hasil total score
     * dari seluruh block terhadap Terrain
     */
    private int getScore(List<Object> blocks) {
        int score = 0;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i) == Terrain.WALL) {
                score -= 1000;
            } else if (blocks.get(i) == Terrain.MUD) {
                score -= 100;
            } else if (blocks.get(i) == Terrain.BOOST) {
                score += 5;
            } else if (blocks.get(i) == Terrain.OIL_SPILL) {
                score -= 100;
            } else if (blocks.get(i) == Terrain.LIZARD) {
                score += 1;
            } else if (blocks.get(i) == Terrain.EMP) {
                score += 5;
            } else if (blocks.get(i) == Terrain.TWEET) {
                score += 2;
            } else {
                score += 2;
            }
        }
        return score;

    }

    /**
     * Mengembalikan list suatu block yang dapat dihinggapi oleh kecepatan masukan
     * terhadap parameter lane dengan posisi awal block berasal dari parameter block
     */
    private List<Object> getBlocksInFront(int lane, int block, int speed) {

        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

    /**
     * Mengecek apakah Terrain dalam blocks bukan merupakan rintangan yang
     * menghambat mobil seperti Mud, Wall, atau Oil Spill
     */
    private Boolean isNotClear(List<Object> blocks) {
        return blocks.contains(Terrain.MUD) || blocks.contains(Terrain.WALL)
                || blocks.contains(Terrain.OIL_SPILL);
    }

}
