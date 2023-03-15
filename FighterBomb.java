import java.awt.*;

// 自機の爆弾
public class FighterBomb{
  int num;// 自分のナンバー
  int i, j;
  int x;// 座標
  int y;
  int l = 48;// 辺の長さ
  int timer;// タイマー
  int bombTime = 120;// 着火するまでの時間
  int explosionTime = 30;// 爆発する時間
  int hp = 0; // 2:set 1: explosion 0: don't exist
  Obstacle[] obs;
  int obsNum;
  Fighter ftr;
  FighterBullet[] ftrBlt;
  int ftrBltNum;
  FighterBomb[] ftrBmb;
  int ftrBmbNum;
  Enemy[] enm;
  int enmNum;
  EnemyBullet[][] enmBlt;
  int enmBltNum;
  Image img;// 爆弾の絵
  Image explosionImg;// 爆発の絵
  int bmbImgX;// 爆弾を描画する部分
  int explosionImgX;// 爆発を描画する部分

  FighterBomb(int num, Image img, Image explosionImg){
    this.num = num;
    this.img = img;
    this.explosionImg = explosionImg;
  }

  public void move(Graphics g, StageCreate sc){// 移動と描画
    if(hp > 0){// 生きているとき
      timer++;
      if(hp == 2){// 爆発していないとき
        if(timer <= 30) bmbImgX = 0;
        else if(timer <= 60) bmbImgX = 1;
        else if(timer <= 90) bmbImgX = 2;
        else bmbImgX = 3;
        g.drawImage(img, x-l/2, y-l/2, x+l/2, y+l/2, 48*bmbImgX, 0, 48*bmbImgX+48, 48, null);
        // g.setColor(Color.yellow);
        // g.fillOval(x-l/2, y-l/2, l, l);
      }else if(hp == 1){// 爆発中
        if(timer < 3) explosionImgX = 0;
        else if(timer < 5) explosionImgX = 1;
        else explosionImgX = 2;
        g.drawImage(explosionImg, x-3*l/2, y-3*l/2, x+3*l/2, y+3*l/2, 48*3*explosionImgX, 0, 48*3*explosionImgX+48*3, 48*3, null);
        // g.setColor(Color.red);
        // g.fillOval(x-3*l/2, y-3*l/2, 3*l, 3*l);

        // 各オブジェクトとの当たり辺定
        for(i=0; i<obsNum; i++){// 障害物
          if(obs[i].hp > 0 && obs[i].kind == 1){
            if(Math.abs(this.x-obs[i].x-obs[i].l/2) < (3*l/2+obs[i].l/2) && Math.abs(this.y-obs[i].y-obs[i].l/2) < (3*l/2+obs[i].l/2)){
              obs[i].hp = 0;
            }
          }
        }
        if(ftr.hp > 1){// 自機
          if(Math.abs(this.x-ftr.x) < (3*l/2+ftr.l/2) && Math.abs(this.y-ftr.y) < (3*l/2+ftr.l/2)){
            ftr.hp = 1;
          }
        }
        for(i=0; i<ftrBltNum; i++){// 自機の弾
          if(ftrBlt[i].hp > 0){
            if(Math.abs(this.x-ftrBlt[i].x) < (3*l/2+ftrBlt[i].l/2) && Math.abs(this.y-ftrBlt[i].y) < (3*l/2+ftrBlt[i].l/2)){
              ftrBlt[i].hp = 0;
            }
          }
        }
        for(i=0; i<ftrBmbNum; i++){// 自機の爆弾
          if(ftrBmb[i].hp == 2 && num != i){
            if(Math.abs(this.x-ftrBmb[i].x) < (3*l/2+ftrBmb[i].l/2) && Math.abs(this.y-ftrBmb[i].y) < (3*l/2+ftrBmb[i].l/2)){
              ftrBmb[i].hp = 1;
              ftrBmb[i].timer = 0;
            }
          }
        }
        for(i=0; i<enmNum; i++){// 敵
          if(enm[i].hp > 1){
            if(Math.abs(this.x-enm[i].x) < (3*l/2+enm[i].l/2) && Math.abs(this.y-enm[i].y) < (3*l/2+enm[i].l/2)){
              enm[i].hp = 1;
              sc.score++;
            }
          }
          for(j=0; j<enmBltNum; j++){// 敵の弾
            if(enmBlt[i][j].hp > 0){
              if(Math.abs(this.x-enmBlt[i][j].x) < (3*l/2+enmBlt[i][j].l/2) && Math.abs(this.y-enmBlt[i][j].y) < (3*l/2+enmBlt[i][j].l/2)){
                enmBlt[i][j].hp = 0;
              }
            }
          }
        }
      }
      if(hp == 2){
        // 敵や自機の弾が爆弾に当たると爆発
        for(i=0; i<ftrBltNum; i++){// 自機の弾と接触
          if(ftrBlt[i].hp > 0){
            if(Math.abs(this.x-ftrBlt[i].x) < (l/2+ftrBlt[i].l/2) && Math.abs(this.y-ftrBlt[i].y) < (l/2+ftrBlt[i].l/2)){
              ftrBlt[i].hp = 0;
              hp =1;
              timer = 0;
              break;
            }
          }
        }
        for(i=0; i<enmNum; i++){// 敵の弾と接触
          for(j=0; j<enmBltNum; j++){
            if(enmBlt[i][j].hp > 0){
              if(Math.abs(this.x-enmBlt[i][j].x) < (l/2+enmBlt[i][j].l/2) && Math.abs(this.y-enmBlt[i][j].y) < (l/2+enmBlt[i][j].l/2)){
                enmBlt[i][j].hp = 0;
                hp = 1;
                timer = 0;
                break;
              }
            }
          }
        }
      }
      if(timer > bombTime && hp == 2){// 時間経過で爆発している状態へ移動
        hp =1;
        timer = 0;
      }
      if(timer > explosionTime && hp == 1){// 時間経過で消滅
        hp = 0;
      }
      //System.out.println(timer);
    }
  }

  public void revive(int x, int y){// 生きている状態にする
      this.x = x;
      this.y = y;
      hp = 2;
      timer = 0;
      ftr.vf = false;
  }
  public void reset(){// リセット
    hp = 0;
    timer = 0;
  }
  public void set(Obstacle[] obs, int obsNum, Fighter ftr, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){// 情報を渡す
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
