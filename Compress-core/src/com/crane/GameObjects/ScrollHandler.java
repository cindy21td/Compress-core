package com.crane.GameObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;

public class ScrollHandler {

	private GameWorld gameWorld;

	private Enemy enemyOne, enemyTwo, enemyThree, enemyFour, enemyFive,
			enemySix, enemySeven;
	private List<Enemy> enemyCollOriginal;
	private List<Enemy> enemyColl;
	
	private List<Integer> posXKnight;
	private List<Integer> posXWizard;

	private Background bgFront, bgBack;

	private Boss boss;
	private boolean bossFight;

	private RunningState state;

	public enum RunningState {
		NORMAL, RUSH;
	}

	public static final int SCROLL_SPEED = -59;

	public enum EnemyType {
		WIZARD, KNIGHT, SUMMONER;
	}

	public ScrollHandler(GameWorld world, float yPos) {

		this.gameWorld = world;
		this.state = RunningState.NORMAL;
		this.bossFight = false;

		initializePosX();

		initializeEnemy();
		

		bgFront = new Background(0, 0, 204, 136, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 136, SCROLL_SPEED);

		boss = new Boss(-157, -80, 157, 120, 10, this);
	}
	
	public void initializePosX() {
		posXWizard = new ArrayList<Integer>();
		posXWizard.add(0);
		posXWizard.add(1);
		posXWizard.add(2);
		posXWizard.add(3);
		posXWizard.add(4);
		posXWizard.add(5);
		posXWizard.add(6);

		
		posXKnight = new ArrayList<Integer>(posXWizard);
	}

	public void initializeEnemy() {
		enemyColl = new ArrayList<Enemy>();
		enemyCollOriginal = new ArrayList<Enemy>();

		enemyOne = new Wizard(20, 20, SCROLL_SPEED, posXWizard);
		enemyTwo = new Wizard(20, 20, SCROLL_SPEED, posXWizard);
		enemyThree = new Wizard(20, 20, SCROLL_SPEED, posXWizard);

		enemyFour = new Knight(22, 22, SCROLL_SPEED - getEnemyRanVelX(), posXKnight);
		enemyFive = new Knight(22, 22, SCROLL_SPEED - getEnemyRanVelX(), posXKnight);
		enemySix = new Knight(22, 22, SCROLL_SPEED - getEnemyRanVelX(), posXKnight);

		enemySeven = new Summoner(18, 18, SCROLL_SPEED + 10);

		enemyColl.add(enemyOne);
		enemyColl.add(enemyTwo);
		enemyColl.add(enemyThree);
		enemyColl.add(enemyFour);
		enemyColl.add(enemyFive);
		enemyColl.add(enemySix);

		enemyColl.add(enemySeven);

		enemyCollOriginal.add(enemyOne);
		enemyCollOriginal.add(enemyTwo);
		enemyCollOriginal.add(enemyThree);
		enemyCollOriginal.add(enemyFour);
		enemyCollOriginal.add(enemyFive);
		enemyCollOriginal.add(enemySix);

		enemyCollOriginal.add(enemySeven);

		setRandomVisibility();
		setRandomVisibility();
		setRandomVisibility();
	}

	public void setRandomVisibility() {
		int ranIndex = MathUtils.random(0, enemyColl.size() - 1);
		Enemy e = enemyColl.get(ranIndex);
		// if (e.getType() == EnemyType.SUMMONER) {
		// ranIndex = MathUtils.random(0, enemyColl.size() - 1);
		// e = enemyColl.get(ranIndex);
		// }
		enemyColl.remove(e);
		e.setIsVisible(true);
	}

	public void updateReady(float delta) {

        bgFront.update(delta);
        bgBack.update(delta);

        // Same with grass
        if (bgFront.isScrolledLeft()) {
			bgFront.reset(bgBack.getTailX());
		} else if (bgBack.isScrolledLeft()) {
			bgBack.reset(bgFront.getTailX());
		}

    }

	
	public void update(float delta) {
		bgFront.update(delta);
		bgBack.update(delta);

		if (bgFront.isScrolledLeft()) {
			bgFront.reset(bgBack.getTailX());
		} else if (bgBack.isScrolledLeft()) {
			bgBack.reset(bgFront.getTailX());
		}

		// Update Enemy
		enemyUpdate(delta, enemyOne);
		enemyUpdate(delta, enemyTwo);
		enemyUpdate(delta, enemyThree);
		enemyUpdate(delta, enemyFour);
		enemyUpdate(delta, enemyFive);
		enemyUpdate(delta, enemySix);

		enemyUpdate(delta, enemySeven);

		if (bossFight) {
			boss.update(delta);
		}

	}

	public void stop() {
		enemyOne.stop();
		enemyTwo.stop();
		enemyThree.stop();
		enemyFour.stop();
		enemyFive.stop();
		enemySix.stop();

		enemySeven.stop();

		bgFront.stop();
		bgBack.stop();

		boss.stop();
	}

	public void onRestart() {
		enemyOne.reset(0, true);
		enemyTwo.reset(0, true);
		enemyThree.reset(0, true);

		enemyFour.reset(getEnemyRanVelX(), true);
		enemyFive.reset(getEnemyRanVelX(), true);
		enemySix.reset(getEnemyRanVelX(), true);

		enemySeven.reset(-10, true);

		enemyColl = new ArrayList<Enemy>(enemyCollOriginal);

		setRandomVisibility();
		setRandomVisibility();
		setRandomVisibility();

		bgFront.onRestart();
		bgBack.onRestart();

		bossFight = false;
		boss.onRestart();
	}

	public boolean collides(Hero hero) {
		if (boss.hasWon()) {
			return false;
		} else {
			if (enemyIsHit(hero)) {
				gameWorld.addScore(1);
			}

			if (bossFight && bossIsHit()) {
				gameWorld.addScore(2);
			}

			return (enemyOne.collides(hero) || enemyTwo.collides(hero)
					|| enemyThree.collides(hero) || enemyFour.collides(hero)
					|| enemyFive.collides(hero) || enemySix.collides(hero) || enemySeven
						.collides(hero) || boss.collides(hero));
		}
	}

	public boolean enemyIsHit(Hero hero) {
		return (enemyOne.isHit(hero) || enemyTwo.isHit(hero)
				|| enemyThree.isHit(hero) || enemyFour.isHit(hero)
				|| enemyFive.isHit(hero) || enemySix.isHit(hero) || enemySeven
					.isHit(hero));
	}

	public boolean bossIsHit() {
		return (boss.isHit(enemyOne) || boss.isHit(enemyTwo)
				|| boss.isHit(enemyThree) || boss.isHit(enemyFour)
				|| boss.isHit(enemyFive) || boss.isHit(enemySix));
	}

	public float getEnemyRanVelX() {
		switch (state) {

		case NORMAL:
			return MathUtils.random(10, 41);

		case RUSH:
			return MathUtils.random(50, 71);

		default:
			return MathUtils.random(10, 41);

		}

	}

	private void enemyUpdate(float delta, Enemy enemy) {
		if (enemy.isVisible()) {
			enemy.update(delta);
			if (enemy.isScrolledLeft()) {
				if (enemy.equals(enemyOne) || enemy.equals(enemyTwo)
						|| enemy.equals(enemyThree)) {
					enemy.reset(0, false);
					enemyColl.add(enemy);

				} else if (enemy.equals(enemySeven)) {
					if (enemy.isAlive()) {
						setBossAlive(true);
						toogleBossFight(true);
					} else {
						enemyColl.add(enemy);
					}
					enemy.reset(-10, false);
				} else {
					enemy.reset(getEnemyRanVelX(), false);
					enemyColl.add(enemy);

				}
				setRandomVisibility();
			}
		}
	}

	public void changeStage(RunningState newStage) {
		state = newStage;
	}

	public void setBossAlive(boolean check) {
		boss.setAlive(check);
	}

	public boolean bossWins() {
		return boss.dropped();
	}

	public Enemy getEnemyOne() {
		return enemyOne;
	}

	public Enemy getEnemyTwo() {
		return enemyTwo;
	}

	public Enemy getEnemyThree() {
		return enemyThree;
	}

	public Enemy getEnemyFour() {
		return enemyFour;
	}

	public Enemy getEnemyFive() {
		return enemyFive;
	}

	public Enemy getEnemySix() {
		return enemySix;
	}

	public Enemy getEnemySeven() {
		return enemySeven;
	}

	public Background getBgFront() {
		return bgFront;
	}

	public Background getBgBack() {
		return bgBack;
	}

	public Boss getBoss() {
		return boss;
	}

	public boolean getBossFight() {
		return bossFight;
	}

	public void toogleBossFight(boolean check) {
		for(Enemy e : enemyColl) {
			e.setBossFight(check);
		}
		if (check) {
			boss.onRestart();
		} else {
			enemyColl.add(enemySeven);
			gameWorld.addScore(10);
		}
		bossFight = check;
	}

}
