package com.game.shooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Sound;

public class SpaceShooterGame extends ApplicationAdapter {

    // Rendering
    SpriteBatch batch;
    BitmapFont font;

    // Player
    Texture playerImg;
    Rectangle player;
    int health = 3;

    // Bullets
    Texture bulletImg;
    Array<Rectangle> bullets;

    // Enemies
    Texture enemyImg;
    Array<Rectangle> enemies;
    float enemySpawnTimer;

    // Score
    int score = 0;

    // Sounds
    Sound shootSound;
    Sound explosionSound;

    // Game States
    enum GameState { MENU, PLAYING, GAME_OVER }
    GameState gameState = GameState.MENU;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Font
        font = new BitmapFont();
        font.getData().setScale(2);

        // Player
        playerImg = new Texture("player.png");
        player = new Rectangle();
        player.width = 64;
        player.height = 64;
        player.x = 400 - player.width / 2f;
        player.y = 30;

        // Bullets
        bulletImg = new Texture("bullet.png");
        bullets = new Array<>();

        // Enemies
        enemyImg = new Texture("enemy.png");
        enemies = new Array<>();
        enemySpawnTimer = 0;

        // Sounds
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
    }

    @Override
    public void render() {

        // ======================
        // MENU SCREEN
        // ======================
        if (gameState == GameState.MENU) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            font.draw(batch, "SPACE SHOOTER", 260, 350);
            font.draw(batch, "Press ENTER to Start", 230, 300);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        // ======================
        // GAME OVER SCREEN
        // ======================
        if (gameState == GameState.GAME_OVER) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            font.draw(batch, "GAME OVER", 300, 350);
            font.draw(batch, "Final Score: " + score, 260, 300);
            font.draw(batch, "Press R to Restart", 240, 250);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                // Reset everything
                score = 0;
                health = 3;
                bullets.clear();
                enemies.clear();
                enemySpawnTimer = 0;
                player.x = 400 - player.width / 2f;
                player.y = 30;
                gameState = GameState.PLAYING;
            }
            return;
        }

        // ======================
        // GAME PLAYING
        // ======================
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();

        // Player movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 300 * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 300 * delta;
        }

        // Player bounds
        if (player.x < 0) player.x = 0;
        if (player.x > 800 - player.width) player.x = 800 - player.width;

        // Fire bullets
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Rectangle bullet = new Rectangle();
            bullet.width = 8;
            bullet.height = 16;
            bullet.x = player.x + player.width / 2f - bullet.width / 2f;
            bullet.y = player.y + player.height;
            bullets.add(bullet);
            shootSound.play();
        }

        // Move bullets
        for (Rectangle bullet : bullets) {
            bullet.y += 500 * delta;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            if (bullets.get(i).y > 600) {
                bullets.removeIndex(i);
            }
        }

        // Spawn enemies
        enemySpawnTimer += delta;
        if (enemySpawnTimer > 1.2f) {
            Rectangle enemy = new Rectangle();
            enemy.width = 64;
            enemy.height = 64;
            enemy.x = (float) Math.random() * (800 - enemy.width);
            enemy.y = 600;
            enemies.add(enemy);
            enemySpawnTimer = 0;
        }

        // Move enemies
        for (Rectangle enemy : enemies) {
            enemy.y -= 150 * delta;
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).y + enemies.get(i).height < 0) {
                enemies.removeIndex(i);
            }
        }

        // Player ↔ Enemy collision
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).overlaps(player)) {
                enemies.removeIndex(i);
                health--;
                if (health <= 0) {
                    gameState = GameState.GAME_OVER;
                }
            }
        }

        // Bullet ↔ Enemy collision
        for (int i = bullets.size - 1; i >= 0; i--) {
            Rectangle bullet = bullets.get(i);
            for (int j = enemies.size - 1; j >= 0; j--) {
                if (bullet.overlaps(enemies.get(j))) {
                    bullets.removeIndex(i);
                    enemies.removeIndex(j);
                    score += 10;
                    explosionSound.play();
                    break;
                }
            }
        }

        // Draw everything
        batch.begin();

        batch.draw(playerImg, player.x, player.y, player.width, player.height);

        for (Rectangle bullet : bullets) {
            batch.draw(bulletImg, bullet.x, bullet.y, bullet.width, bullet.height);
        }

        for (Rectangle enemy : enemies) {
            batch.draw(enemyImg, enemy.x, enemy.y, enemy.width, enemy.height);
        }

        font.draw(batch, "Score: " + score, 20, 580);
        font.draw(batch, "Health: " + health, 20, 550);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerImg.dispose();
        bulletImg.dispose();
        enemyImg.dispose();
        shootSound.dispose();
        explosionSound.dispose();
        font.dispose();
    }
}

