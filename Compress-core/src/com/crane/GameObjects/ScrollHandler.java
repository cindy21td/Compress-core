package com.crane.GameObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.crane.GameWorld.GameWorld;

public class ScrollHandler {

	private GameWorld gameWorld;

	private Enemy enemyOne, enemyTwo, enemyThree, enemyFour, enemyFive,
			enemySix;
	private List<Enemy> enemyCollOriginal;
	private List<Enemy> enemyColl;

	private Background bgFront, bgBack;

	private Projectile flame;

	private Boss boss;
	private boolean bossFight;

	private RunningState state;

	public enum RunningState {
		NORMAL, RUSH;
	}

	public static final int SCROLL_SPEED = -59;

	public enum EnemyType {
		WIZARD, KNIGHT;
	}

	public ScrollHandler(GameWorld world, float yPos) {

		this.gameWorld = world;
		this.state = RunningState.NORMAL;
		this.bossFight = false;

		initializeEnemy();

		flame = new Projectile(enemyOne.getX() - 10, enemyOne.getY(), 10, 10,
				SCROLL_SPEED - enemyOne.getVelX());

		bgFront = new Background(0, 0, 204, 136, SCROLL_SPEED);
		bgBack = new Background(204, 0, 204, 136, SCROLL_SPEED);

		boss = new Boss(-150, 0, 150, 136, 10, this);
	}

	public void initializeEnemy() {
		enemyColl = new ArrayList<Enemy>();
		enemyCollOriginal = new ArrayList<Enemy>();

		enemyOne = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 20, 20,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.WIZARD);
		enemyTwo = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 20, 20,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.WIZARD);
		enemyThree = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 20, 20,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.WIZARD);

		enemyFour = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 22, 22,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.KNIGHT);
		enemyFive = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 22, 22,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.KNIGHT);
		enemySix = new Enemy(getEnemyRanPosX(), getEnemyRanPosY(), 22, 22,
				SCROLL_SPEED - getEnemyRanVelX(), EnemyType.KNIGHT);

		enemyColl.add(enemyOne);
		enemyColl.add(enemyTwo);
		enemyColl.add(enemyThree);
		enemyColl.add(enemyFour);
		enemyColl.add(enemyFive);
		enemyColl.add(enemySix);
		
		enemyCollOriginal.add(enemyOne);
		enemyCollOriginal.add(enemyTwo);
		enemyCollOriginal.add(enemyThree);
		enemyCollOriginal.add(enemyFour);
		enemyCollOriginal.add(enemyFive);
		enemyCollOriginal.add(enemySix);

		setRandomVisibility();
		setRandomVisibility();
		setRandomVisibility();
	}

	public void setRandomVisibility() {
		int ranIndex = MathUtils.random(0, enemyColl.size() - 1);
		Enemy e = enemyColl.get(ranIndex);
		enemyColl.remove(e);
			e.setIsVisible(true);
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

		if (bossFight) {
			boss.update(delta);
		}

	}

	public void stop() {
		enemyOne.stop();
		enemyTwo.stop();
		enemyThree.stop();

		bgFront.stop();
		bgBack.stop();

		boss.stop();
	}

	public void onRestart() {
		enemyOne.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
		enemyTwo.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
		enemyThree.reset(getEnemyRanPosX(), getEnemyRanPosY(),
				getEnemyRanVelX());
		
		enemyFour.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
		enemyFive.reset(getEnemyRanPosX(), getEnemyRanPosY(), getEnemyRanVelX());
		enemySix.reset(getEnemyRanPosX(), getEnemyRanPosY(),
				getEnemyRanVelX());

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
		if (enemyIsHit(hero)) {
			gameWorld.addScore(1);
		}

		if (bossFight && bossIsHit()) {
			gameWorld.addScore(2);
		}

		return (enemyOne.collides(hero) || enemyTwo.collides(hero)
				|| enemyThree.collides(hero) || enemyFour.collides(hero)
				|| enemyFive.collides(hero) || enemySix.collides(hero));
	}

	public boolean enemyIsHit(Hero hero) {
		return (enemyOne.isHit(hero) || enemyTwo.isHit(hero)
				|| enemyThree.isHit(hero) || enemyFour.isHit(hero)
				|| enemyFive.isHit(hero) || enemySix.isHit(hero));
	}

	public boolean bossIsHit() {
		return (boss.collides(enemyOne) || boss.collides(enemyTwo)
				|| boss.collides(enemyThree) || boss.collides(enemyFour)
				|| boss.collides(enemyFive) || boss.collides(enemySix));
	}

	public float getEnemyRanPosX() {
		return MathUtils.random(204, 235);
	}

	public float getEnemyRanPosY() {
		if (bossFight) {
			return MathUtils.random(80, 107);
		}

		return MathUtils.random(50, 112);
	}

	public float getEnemyRanVelX() {
		switch (state) {

		case NORMAL:
			return MathUtils.random(0, 41);

		case RUSH:
			return MathUtils.random(50, 71);

		default:
			return MathUtils.random(0, 41);

		}

	}

	private void enemyUpdate(float delta, Enemy enemy) {
		if (enemy.isVisible()) {
			enemy.update(delta);
			if (enemy.isScrolledLeft()) {

				enemy.reset(getEnemyRanPosX(), getEnemyRanPosY(),
						getEnemyRanVelX());
				enemyColl.add(enemy);
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
		return boss.wins();
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

	public Projectile getProjectile() {
		return flame;
	}

	public void toogleBossFight(boolean check) {
		if (check) {
			boss.onRestart();
		}
		bossFight = check;
	}

}
