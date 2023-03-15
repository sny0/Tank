import java.awt.*;

// 自機の打上弾
public class FighterFlyBullet{
  int i, j;
  int x;// 座標
  int y;
  int v = 6;// 速さ
  int vx;// 速さのベクトル
  int vy;
  int rad = 0;
  int l;// 辺の長さ
  int timer;// タイマー
  int flightTime = 60; // time of flight
  int explosionTime = 15; // time of explosion
  int hp; // 2:fly 1:impact 0:not exist
  Fighter ftr;
  Obstacle[] obs;
  int obsNum;
  FighterBullet[] ftrBlt;
  int ftrBltNum;
  FighterBomb[] ftrBmb;
  int ftrBmbNum;
  Enemy[] enm;
  int enmNum;
  EnemyBullet[][] enmBlt;
  int enmBltNum;
  Image img;;// 打上弾の絵
  int imgX;// 打上弾の描画する部分
  Image explosionImg;// 爆発の絵
  int explosionImgX;// 爆発の描画する部分
  FighterFlyBullet(Image img, Image explosionImg){
    this.img = img;
    this.explosionImg = explosionImg;
  }

  public void move(Graphics g, StageCreate sc){// 移動と描画
    if(hp > 0){
      timer++;
      if(hp == 2){// 空中にいるとき
        if(timer <= 7 || timer >= flightTime - 7) imgX = 0;
        else if(timer <= 14 || timer >= flightTime - 14) imgX = 1;
        else if(timer <= 21 || timer >= flightTime - 21) imgX = 2;
        else imgX = 3;
        g.drawImage(img, x-48/2, y-48/2, x+48/2, y+48/2, 48*imgX, 0, 48*imgX+48, 48, null);
        // if(timer < flightTime/2) l = 6 + (int)(timer*2/3);
        // else l = 6 + (int)((flightTime - timer)*2/3);
        // g.setColor(Color.cyan);
        // g.fillOval(x-l/2, y-l/2, l, l);

        x += vx;
        y += vy;
      }else if(hp == 1){// 爆発しているとき
        if(timer < 3) explosionImgX = 0;
        else if(timer < 5) explosionImgX = 1;
        else explosionImgX = 2;
        g.drawImage(explosionImg, x-48, y-48, x+48, y+48, 48*2*explosionImgX, 0, 48*2*explosionImgX+48*2, 48*2, null);
        l = 48*2;
        // g.setColor(Color.pink);
        // g.fillOval(x-l/2, y-l/2, l, l);

        // 爆発と各オブジェクトの当たり判定
        for(i=0; i<obsNum; i++){// 障害物
          if(obs[i].hp > 0 && obs[i].kind == 1){
            if(Math.abs(this.x-obs[i].x-obs[i].l/2) < (l/2+obs[i].l/2) && Math.abs(this.y-obs[i].y-obs[i].l/2) < (l/2+obs[i].l/2)){
              obs[i].hp = 0;
            }
          }
        }
        if(ftr.hp > 1){// 自機
          if(Math.abs(this.x-ftr.x) < (l/2+ftr.l/2) && Math.abs(this.y-ftr.y) < (l/2+ftr.l/2)){
            ftr.hp = 1;
          }
        }
        for(i=0; i<ftrBltNum; i++){// 自機の弾
          if(ftrBlt[i].hp > 0){
            if(Math.abs(this.x-ftrBlt[i].x) < (l/2+ftrBlt[i].l/2) && Math.abs(this.y-ftrBlt[i].y) < (l/2+ftrBlt[i].l/2)){
              ftrBlt[i].hp = 0;
            }
          }
        }
        for(i=0; i<ftrBmbNum; i++){// 自機の爆弾
          if(ftrBmb[i].hp == 2){
            if(Math.abs(this.x-ftrBmb[i].x) < (l/2+ftrBmb[i].l/2) && Math.abs(this.y-ftrBmb[i].y) < (l/2+ftrBmb[i].l/2)){
              ftrBmb[i].hp = 0;
              ftrBmb[i].timer = 0;
            }
          }
        }
        for(i=0; i<enmNum; i++){// 敵
          if(enm[i].hp > 1){
            if(Math.abs(this.x-enm[i].x) < (l/2+enm[i].l/2) && Math.abs(this.y-enm[i].y) < (l/2+enm[i].l/2)){
              enm[i].hp = 1;
              sc.score++;
            }
          }
          for(j=0; j<enmBltNum; j++){// 敵の弾
            if(enmBlt[i][j].hp > 0){
              if(Math.abs(this.x-enmBlt[i][j].x) < (l/2+enmBlt[i][j].l/2) && Math.abs(this.y-enmBlt[i][j].y) < (l/2+enmBlt[i][j].l/2)){
                enmBlt[i][j].hp = 0;
              }
            }
          }
        }
      }
      if(timer > flightTime && hp == 2){// 時間経過で爆発している状態へ移動
        hp =1;
        timer = 0;
      }
      if(timer > explosionTime && hp == 1){// 時間経過で消滅した状態へ移動
        hp = 0;
      }
    }
  }
  public void revive(int x, int y, int rad){// 生きている状態にする
    this.rad = rad;
    this.x = x + (int)((Math.sqrt(2)*ftr.tl+1)*Math.cos(Math.toRadians(rad)));
    this.y = y + (int)((Math.sqrt(2)*ftr.tl+1)*Math.sin(Math.toRadians(rad)));
    vx =(int) (v*(Math.cos(Math.toRadians(rad))));
    vy =(int) (v*(Math.sin(Math.toRadians(rad))));
    hp = 2;
    timer = 0;
    ftr.sf = false;
    explosionImgX = 0;
  }
  public void reset(){// リセット
    hp = 0;
    timer = 0;
  }
  public void set(Obstacle[] obs , int obsNum, Fighter ftr, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){// 情報をセット
    this.obs = obs;
    this.obsNum = obsNum;
    this.ftr = ftr;
    this.ftrBlt = ftrBlt;
    this.ftrBltNum = ftrBltNum;
    this.ftrBmb = ftrBmb;
    this.ftrBmbNum = ftrBmbNum;
    this.enm = enm;
    this.enmNum = enmNum;
    this.enmBlt = enmBlt;
    this.enmBltNum = enmBltNum;
  }
}
