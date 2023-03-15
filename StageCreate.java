import java.awt.*;
import java.awt.Toolkit;

//  ステージ作成、オブジェクトの操作など
public class StageCreate{
  //Stage1 s1;
  int i, j;
  int remain;// 残機
  int stage;// ステージ
  int score;// スコア
  boolean clear;// クリアかどうか
  int gameW[];// ゲーム画面の横
  int gameH[];// ゲーム画面の縦
  int ftrBltNum = 5;// 自機の通常弾の数
  int obsNum = 200;// 障害物の数
  Image[] obstacleImg;// 障害物の絵
  Image ftrImg;// 自機の絵
  Image[] enmImg;// 敵の絵
  Image[] bltImg;// 通常弾の絵
  Image bmbImg;// 爆弾の絵
  Image flyBltImg;// 打上弾の絵
  Image bmbExplosionImg;// 爆打の爆発の絵
  Image flyBltExplosionImg;// 打上弾の爆発の絵
  Image explosionImg;// 機体の爆発の絵
  FighterFlyBullet ftrFlyBlt;// 自機の打上弾
  int ftrBmbNum = 3;// 自機の爆弾の数
  Obstacle[] obs;// 障害物
  Fighter ftr;// 自機
  FighterBullet[] ftrBlt;// 自機の通常弾
  FighterBomb[] ftrBmb;// 自機の爆弾
  Enemy[] enm;// 敵
  int enmNum = 10;// 敵の数
  EnemyBullet[][] enmBlt;// 敵の通常弾
  int enmBltNum = 5;// 敵の通常弾の数
  EnemyFlyBullet[][] enmFlyBlt;// 敵の打上弾
  int enmFlyBltNum = 2;// 敵の打上弾の数

  StageCreate(int[] w, int[] h){
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = w[0];
    gameW[1] = w[1];
    gameH[0] = h[0];
    gameH[1] = h[1];
    // イラストを読み込む
    obstacleImg = new Image[2];
    obstacleImg[0] = Toolkit.getDefaultToolkit().getImage("img/illust/iron_Rust.png");
    obstacleImg[1] = Toolkit.getDefaultToolkit().getImage("img/illust/brock.png");
    ftrImg = Toolkit.getDefaultToolkit().getImage("img/illust/Fighter.png");
    enmImg = new Image[4];
    enmImg[0] = Toolkit.getDefaultToolkit().getImage("img/illust/Enemy_Green.png");
    enmImg[1] = Toolkit.getDefaultToolkit().getImage("img/illust/Enemy_Brown.png");
    enmImg[2] = Toolkit.getDefaultToolkit().getImage("img/illust/Enemy_White.png");
    enmImg[3] = Toolkit.getDefaultToolkit().getImage("img/illust/Enemy_Purple.png");
    bltImg = new Image[2];
    bmbImg = Toolkit.getDefaultToolkit().getImage("img/illust/Bomb.png");
    bltImg[0] = Toolkit.getDefaultToolkit().getImage("img/illust/Tank_Bullet_10B.png");
    bltImg[1] = Toolkit.getDefaultToolkit().getImage("img/illust/Tank_Bullet_10R.png");
    flyBltImg = Toolkit.getDefaultToolkit().getImage("img/illust/Fly_Bullet.png");
    bmbExplosionImg = Toolkit.getDefaultToolkit().getImage("img/illust/Explosion_Bomb.png");
    flyBltExplosionImg = Toolkit.getDefaultToolkit().getImage("img/illust/Explosion_Fly.png");
    explosionImg = Toolkit.getDefaultToolkit().getImage("img/illust/Explosion_336.png");
    ftr = new Fighter(100, 500, gameW, gameH, ftrImg, explosionImg);
    ftrBlt = new FighterBullet[ftrBltNum];
    for(i=0; i<ftrBltNum; i++){
      ftrBlt[i] = new FighterBullet(gameW, gameH, i, bltImg[0]);
    }
    ftrFlyBlt = new FighterFlyBullet(flyBltImg, flyBltExplosionImg);
    obs = new Obstacle[obsNum];
    for(i=0; i<obsNum; i++){
      obs[i] = new Obstacle(64, 64, obstacleImg);
    }
    ftrBmb = new FighterBomb[ftrBmbNum];
    for(i=0; i<ftrBmbNum; i++){
      ftrBmb[i] = new FighterBomb(i, bmbImg, bmbExplosionImg);
    }
    enm = new Enemy[enmNum];
    for(i=0; i<enmNum; i++){
      enm[i] = new Enemy(1000, 500, gameW, gameH, i, enmImg, explosionImg);
    }
    enmBlt = new EnemyBullet[enmNum][enmBltNum];
    for(i=0; i<enmNum; i++){
      for(j=0; j<enmBltNum; j++){
        enmBlt[i][j] = new EnemyBullet(gameW, gameH, i, j, bltImg);
      }
    }
    enmFlyBlt = new EnemyFlyBullet[enmNum][enmFlyBltNum];
    for(i=0; i<enmNum; i++){
      for(j=0; j<enmFlyBltNum; j++){
        enmFlyBlt[i][j] = new EnemyFlyBullet(flyBltImg, flyBltExplosionImg);
      }
    }

    // 情報を渡す
    ftr.set(obs, obsNum, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
    for(i=0; i<enmNum; i++){
      enm[i].set(ftr, obs, obsNum, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum, enmFlyBlt, enmFlyBltNum);
    }
    for(i=0; i<ftrBltNum; i++){
      ftrBlt[i].set(obs, obsNum, ftr, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
    }
    ftrFlyBlt.set(obs, obsNum, ftr, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
    for(i=0; i<ftrBmbNum; i++){
      ftrBmb[i].set(obs, obsNum, ftr, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
    }
    for(i=0; i<enmNum; i++){
      for(j=0; j<enmBltNum; j++){
        enmBlt[i][j].set(obs, obsNum, ftr, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
      }
      for(j=0; j<enmFlyBltNum; j++){
        enmFlyBlt[i][j].set(obs, obsNum, ftr, ftrBlt, ftrBltNum, ftrBmb, ftrBmbNum, enm, enmNum, enmBlt, enmBltNum);
      }
    }
  }

  public void move(Graphics g, boolean start){// 判定と描画
    if(start){
      makeFtrBullet: if(ftr.sf && ftr.hp > 0){// 自機の弾を作る
        for(i=0; i<ftrBltNum; i++){
          if(ftr.bltKind == 0){
            if(ftrBlt[i].hp == 0){
              ftrBlt[i].revive(ftr.x, ftr.y, ftr.turretRad);
              break makeFtrBullet;
            }
          }else{
            if(ftrFlyBlt.hp == 0){
              ftrFlyBlt.revive(ftr.x, ftr.y, ftr.turretRad);
              break makeFtrBullet;
            }
          }
        }
      }
      makeFtrBomb: if(ftr.vf){// 敵の弾の発射の判断をし弾を作る
        for(i=0; i<ftrBmbNum; i++){
          if(ftrBmb[i].hp == 0){
            ftrBmb[i].revive(ftr.x, ftr.y);
            break makeFtrBomb;
          }
        }
      }
      for(i=0; i<enmNum; i++){
        enm[i].makeBullet(enm[i].judgeFire(g));
      }
    }
    for(i=0; i<obsNum; i++){// 障害物の判定と描画
      obs[i].move(g);
    }
    for(i=0; i<ftrBmbNum; i++){// 自機の爆弾の判定と描画
      ftrBmb[i].move(g, this);
    }
    ftr.move(g, start);// 自機の判定と描画
    for(i=0; i<ftrBltNum; i++){// 自機の弾の判定と描画
      ftrBlt[i].move(g, this);
    }
    ftrFlyBlt.move(g, this);// 自機の打上弾の判定と描画
    for(i=0; i<enmNum; i++){// 敵の判定と描画
      enm[i].move(g, start);
    }
    for(i=0; i<enmNum; i++){
      for(j=0; j<enmBltNum; j++){// 敵の弾の判定と衝突
        enmBlt[i][j].move(g, this);
      }
      for(j=0; j<enmFlyBltNum; j++){// 敵の打上弾の判定と衝突
        enmFlyBlt[i][j].move(g, this);
      }
    }
    for(i=0; i<enmNum; i++){
      if(enm[i].hp > 0){
        break;
      }
      if(i == enmNum - 1){
        clear = true;// すべての敵のhpが0になったらステージクリア
      }
    }
  }

  public void setStage(){ // ステージのオブジェクトの場所を指定してhpを与える
    ftr.bltKind = 0;
    if(stage == 1){// ステージ１
      ftr.create(64+48*4, 64+48*7, 0);
      enm[0].create(64+48*20, 64+48*7, 180, 0);
      for(i=0; i<5; i++){
        obs[i].create(64+48*11, 64+48*(5+i), 1);
        obs[i+5].create(64+48*12, 64+48*(5+i), 1);
        obs[i+10].create(64+48*13, 64+48*(5+i), 0);
      }
    }
    if(stage == 0){
      ftr.create(64+48*2, 64+48*7, 0);
      enm[0].create(64+48*21, 64+48*2, 180, 0);
      enm[1].create(64+48*21, 64+48*12, 180, 0);
      for(i=0; i<15; i++){
        obs[i].create(64+48*10+48*i, 64+48*6, 0);
        obs[i+15].create(64+48*10+48*i, 64+48*7, 0);
        obs[i+30].create(64+48*10+48*i, 64+48*8, 0);
      }
    }
    if(stage == 2){// ステージ２
      ftr.create(64+48*2, 64+48*2, 0);
      enm[0].create(64+48*2, 64+48*12, 180, 0);
      enm[1].create(64+48*7, 64+48*1, 180, 0);
      enm[2].create(64+48*13, 64+48*12, 180, 0);
      enm[3].create(64+48*20, 64+48*1, 180, 0);
      enm[4].create(64+48*22, 64+48*6, 180, 0);
      enm[5].create(64+48*22, 64+48*12, 180, 0);
      for(i=0; i<7; i++){
        obs[i].create(64+48*4, 64+48*i, 0);
        obs[i+7].create(64+48*11, 64+48*i, 0);
      }
      for(i=0; i<6; i++){
        obs[i+14].create(64+48*7, 64+48*9+48*i, 0);
      }
      for(i=0; i<9; i++){
        obs[i+20].create(64+48*16, 64+48*6+48*i, 0);
      }
    }
    if(stage == 0){
      ftr.create(64+48*2, 64+48*7, 0);
      enm[0].create(64+48*22, 64+48*7, 180, 1);
      for(i=0; i<8; i++){
        obs[i].create(64+48*i, 64+48*2, 1);
        obs[i+8].create(64+48*i, 64+48*3, 1);
        obs[i+16].create(64+48*i, 64+48*4, 1);
        obs[i+24].create(64+48*i, 64+48*10, 1);
        obs[i+32].create(64+48*i, 64+48*11, 1);
        obs[i+40].create(64+48*i, 64+48*12, 1);
      }
      for(i=0; i<4; i++){
        obs[i+48].create(64+48*4+48*i, 64+48*5, 1);
        obs[i+52].create(64+48*4+48*i, 64+48*6, 1);
        obs[i+56].create(64+48*4+48*i, 64+48*7, 1);
        obs[i+60].create(64+48*4+48*i, 64+48*8, 1);
        obs[i+64].create(64+48*4+48*i, 64+48*9, 1);
        obs[i+68].create(64+48*20, 64+48*5+48*i, 0);
        obs[i+72].create(64+48*20+48*i, 64+48*9, 0);
        obs[i+76].create(64+48*24, 64+48*6+48*i, 0);
        obs[i+80].create(64+48*21+48*i, 64+48*5, 0);
      }
    }
    if(stage == 3){// ステージ３
      ftr.create(64+48*3, 64+48*7, 0);
      enm[0].create(64+48*11, 64+48*3, 180, 0);
      enm[1].create(64+48*14, 64+48*7, 180, 0);
      enm[2].create(64+48*11, 64+48*12, 180, 0);
      enm[3].create(64+48*21, 64+48*2, 180, 1);
      enm[4].create(64+48*21, 64+48*12, 180, 1);
      for(i=0; i<6; i++){
        obs[i].create(64+48*7, 64+48*5+48*i, 1);
        obs[i+6].create(64+48*8, 64+48*5+48*i, 1);
      }
      for(i=0; i<5; i++){
        obs[i+12].create(64+48*16, 64+48*i, 0);
        obs[i+17].create(64+48*17, 64+48*i, 0);
        obs[i+22].create(64+48*16, 64+48*10+48*i, 0);
        obs[i+27].create(64+48*17, 64+48*10+48*i, 0);
      }
    }
    if(stage == 4){// ステージ４
      ftr.create(64+48*2, 64+48*12, 0);
      enm[0].create(64+48*15, 64+48*2, 180, 2);
      enm[1].create(64+48*22, 64+48*9, 180, 2);
      obs[0].create(64+48*4, 64+48*9, 0);
      obs[1].create(64+48*5, 64+48*9, 0);
      obs[2].create(64+48*5, 64+48*10, 0);
      obs[3].create(64+48*18, 64+48*4, 0);
      obs[4].create(64+48*18, 64+48*5, 0);
      obs[5].create(64+48*19, 64+48*5, 0);
    }
    if(stage == 5){// ステージ５
      ftr.create(64+48*2, 64+48*12, 0);
      enm[0].create(64+48*15, 64+48*2, 180, 3);
      enm[1].create(64+48*22, 64+48*9, 180, 3);
      obs[0].create(64+48*3, 64+48*9, 0);
      obs[1].create(64+48*4, 64+48*9, 0);
      obs[2].create(64+48*5, 64+48*9, 0);
      obs[3].create(64+48*5, 64+48*10, 0);
      obs[4].create(64+48*5, 64+48*11, 0);
      obs[5].create(64+48*18, 64+48*3, 0);
      obs[6].create(64+48*18, 64+48*4, 0);
      obs[7].create(64+48*18, 64+48*5, 0);
      obs[8].create(64+48*19, 64+48*5, 0);
      obs[9].create(64+48*20, 64+48*5, 0);
    }
    if(stage == 6){// ステージ６
      ftr.create(64+48*7, 64+48*7, 0);
      enm[0].create(64+48*9, 64+48*2, 180, 0);
      enm[1].create(64+48*9, 64+48*12, 180, 0);
      enm[2].create(64+48*17, 64+48*1, 180, 1);
      enm[3].create(64+48*17, 64+48*13, 180, 1);
      for(i=0; i<15; i++){
        obs[i].create(64, 64+48*i, 0);
        obs[i+15].create(64+48*1, 64+48*i, 0);
        obs[i+30].create(64+48*2, 64+48*i, 0);
        obs[i+45].create(64+48*3, 64+48*i, 0);
        obs[i+60].create(64+48*4, 64+48*i, 0);
        obs[i+75].create(64+48*20, 64+48*i, 0);
        obs[i+90].create(64+48*21, 64+48*i, 0);
        obs[i+105].create(64+48*22, 64+48*i, 0);
        obs[i+120].create(64+48*23, 64+48*i, 0);
        obs[i+135].create(64+48*24, 64+48*i, 0);
      }
      for(i=0; i<5; i++){
        obs[i+150].create(64+48*12, 64+48*i, 0);
        obs[i+155].create(64+48*13, 64+48*i, 0);
        obs[i+160].create(64+48*12, 64+48*10+48*i, 0);
        obs[i+165].create(64+48*13, 64+48*10+48*i, 0);
        obs[i+170].create(64+48*12, 64+48*5+48*i, 1);
        obs[i+175].create(64+48*13, 64+48*5+48*i, 1);
      }
    }
    if(stage == 7){// ステージ７
      ftr.create(64+48*12, 64+48*7, 90);
      enm[0].create(64+48*1, 64+48*4, 0, 1);
      enm[1].create(64+48*1, 64+48*10, 0, 1);
      enm[2].create(64+48*7, 64+48*1, 90, 1);
      enm[3].create(64+48*7, 64+48*13, 270, 1);
      enm[4].create(64+48*17, 64+48*1, 90, 1);
      enm[5].create(64+48*17, 64+48*13, 270, 1);
      enm[6].create(64+48*23, 64+48*4, 180 , 1);
      enm[7].create(64+48*23, 64+48*10, 180, 1);
      //obs[i].create(64+48*3, 64+48*3+48*i, 1);
      for(i=0; i<9; i++){
        obs[i+9].create(64+48*4, 64+48*3+48*i, 1);
        obs[i+18].create(64+48*20, 64+48*3+48*i, 1);
        obs[i+27].create(64+48*21, 64+48*3+48*i, 1);
      }
      for(i=0; i<15; i++){
        obs[i+36].create(64+48*5+48*i, 64+48*3, 1);
        obs[i+51].create(64+48*5+48*i, 64+48*4, 1);
        obs[i+66].create(64+48*5+48*i, 64+48*10, 1);
        obs[i+81].create(64+48*5+48*i, 64+48*11, 1);
      }
    }
    if(stage == 8){// ステージ８
      ftr.create(64+48*12, 64+48*2, 90);
      enm[0].create(64+48*2, 64+48*2, 0, 1);
      enm[1].create(64+48*22, 64+48*2, 180, 1);
      enm[2].create(64+48*9, 64+48*13, 270, 0);
      enm[3].create(64+48*15, 64+48*13, 270, 0);
      enm[4].create(64+48*2, 64+48*12, 0, 3);
      enm[5].create(64+48*22, 64+48*12, 180, 3);
      enm[6].create(64+48*2, 64+48*6, 0, 2);
      enm[7].create(64+48*22, 64+48*6, 180, 2);
      for(i=0; i<9; i++){
        obs[i].create(64+48*7, 64+48*i, 1);
        obs[i+9].create(64+48*8, 64+48*i, 1);
        obs[i+18].create(64+48*16, 64+48*i, 1);
        obs[i+27].create(64+48*17, 64+48*i, 1);
      }
      for(i=0; i<2; i++){
        obs[i+36].create(64+48*9, 64+48*7+48*i, 0);
        obs[i+38].create(64+48*10, 64+48*7+48*i, 0);
        obs[i+40].create(64+48*14, 64+48*7+48*i, 0);
        obs[i+42].create(64+48*15, 64+48*7+48*i, 0);
      }
    }
  }
  public void reload(){// ロードする
    ftr.bltKind = 0;
    ftr.recreate();
    for(i=0; i<enmNum; i++){
      enm[i].recreate();
    }
    for(i=0; i<obsNum; i++){
      obs[i].recreate();
    }
  }
  public void reset(){// リセットする
    ftr.reset();
    for(i=0; i<ftrBltNum; i++){
      ftrBlt[i].reset();
    }
    for(i=0; i<ftrBmbNum; i++){
      ftrBmb[i].reset();
    }
    ftrFlyBlt.reset();
    for(i=0; i<enmNum; i++){
      enm[i].reset();
      for(j=0; j<enmBltNum; j++){
        enmBlt[i][j].reset();
      }
      for(j=0; j<enmFlyBltNum; j++){
        enmFlyBlt[i][j].reset();
      }
    }
    for(i=0; i<obsNum; i++){
      obs[i].reset();
    }
  }
  public void allReset(){//全てのオブジェクトのhpを0にする
    ftr.reset();
    for(i=0; i<ftrBltNum; i++){
      ftrBlt[i].reset();
    }
    for(i=0; i<ftrBmbNum; i++){
      ftrBmb[i].reset();
    }
    ftrFlyBlt.reset();
    for(i=0; i<enmNum; i++){
      enm[i].allReset();
      for(j=0; j<enmBltNum; j++){
        enmBlt[i][j].reset();
      }
      for(j=0; j<enmFlyBltNum; j++){
        enmFlyBlt[i][j].reset();
      }
    }
    for(i=0; i<obsNum; i++){
      obs[i].allReset();
    }
  }
}
