import java.awt.*;
import java.awt.geom.AffineTransform;

// 自機の通常弾
public class FighterBullet{
  int i, j;
  int num;// 自分のナンバー
  private int[] gameW;
  private int[] gameH;
  int x;// 座標
  int y;
  int px;// 前の座標
  int py;
  int v = 6;// 速度
  int vx;// 速度のベクトル
  int vy;
  int rad = 0;// 角度
  int l = 5;// 辺の長さ
  int hp;// hp
  int reflection = 0;// 反射した回数
  Image bltImg;// 弾の絵
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

  FighterBullet(int[] w, int[] h, int num, Image bltImg){// コンストラクタ
    this.num = num;
    this.bltImg = bltImg;
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = w[0];
    gameW[1] = w[1];
    gameH[0] = h[0];
    gameH[1] = h[1];
  }

  public void move(Graphics g, StageCreate sc){// 移動と描画
    judgeHit: if(hp > 0){
      // 弾の描画
      Graphics2D g2 = (Graphics2D) g;
      AffineTransform at = g2.getTransform();
      at.setToRotation(Math.toRadians(rad), x, y);// 描画の回転の角度と中心点を設定
		  g2.setTransform(at);// 設定した角度をセットする
      g.drawImage(bltImg, x-l, y-l, null);// 弾の絵を描画
      at.setToRotation(Math.toRadians(0));// 描画の角度を0に戻す
		  g2.setTransform(at);
      // g.setColor(Color.black);
      // g.fillOval(x-l, y-l, 2*l, 2*l);

      // 弾の移動
      vx =(int) (v*(Math.cos(Math.toRadians(rad))));
      vy =(int) (v*(Math.sin(Math.toRadians(rad))));

      //System.out.println(vx+","+vy);
      px = x;
      py = y;
      x += vx;
      y += vy;

      // 壁との衝突
      if(x-l <= gameW[0] || x+l >= gameW[1]){// 横の壁と衝突したとき
        rad = 180 - rad;// 弾の角度を変化
        reflection ++;// 弾が壁と衝突した回数を1増やす
        if(reflection == 2) hp = 0;// もし2回衝突したら消滅
        if(x-l <= gameW[0]) x = gameW[0]+l+1;// x座標を壁の内側にする
        if(x+l >= gameW[1]) x = gameW[1]-l-1;
      }
      if(y-l <= gameH[0] || y+l >= gameH[1]){// 縦の壁と衝突したとき
        rad = -1 * rad;// 弾の角度を変化
        reflection ++;// 衝突した回数を1増やす
        if(reflection == 2) hp = 0;// もし2回衝突したら消滅
        if(y-l <= gameH[0]) y = gameH[0]+l+1;// y座標を壁の内側にする
        if(y+l >= gameH[1]) y = gameH[1]-l-1;
      }
      // 障害物との衝突
      for(i=0; i<obsNum; i++){
        if(obs[i].hp > 0){// 障害物が生きているとき
          if(x >= obs[i].x && x <= obs[i].x+48 && y >= obs[i].y && y <= obs[i].y+48){// 障害物と衝突しているか判定
            if(px <= obs[i].x || px >= obs[i].x+48){// 横から衝突したとき
              rad = 180 - rad;// 弾の角度を変化
              if(px < obs[i].x) x = obs[i].x-l-1;// x座標を障害物の外側にする
              if(px > obs[i].x+48) x = obs[i].x+48+l+1;
            }
            if(py <= obs[i].y || py >= obs[i].y+48){// 縦から衝突したとき
              rad = -1 * rad;// 弾の角度を変化
              if(py < obs[i].y) y = obs[i].y-l-1;// y座標を障害物の外側にする
              if(py > obs[i].y+48) y = obs[i].y+48+l+1;
            }
            reflection ++;// 衝突した回数を増やす
            if(reflection == 2){// 2回衝突したら消滅
              hp = 0;
              break judgeHit;
            }
          }
        }
      }

      // 角度を(-180,180)に変換
      while (rad >= 180){
        rad -= 360;
      }
      while (rad <= -180){
        rad += 360;
      }

      // 衝突判定
      if(ftr.hp > 1 && reflection != 0){// 自機
        if(Math.abs(this.x-ftr.x) < (l+ftr.l/2) && Math.abs(this.y-ftr.y) < (l+ftr.l/2)){
          hp = 0;
          ftr.hp = 1;
          break judgeHit;
        }
      }
      for(i=0; i<ftrBltNum; i++){// 自機の通常弾
        if(i != num && ftrBlt[i].hp > 0){
          if(Math.abs(this.x-ftrBlt[i].x) < (l+ftrBlt[i].l) && Math.abs(this.y-ftrBlt[i].y) < (l+ftrBlt[i].l)){
            hp = 0;
            ftrBlt[i].hp = 0;
            break judgeHit;
          }
        }
      }
      for(i=0; i<enmNum; i++){// 敵
        if(enm[i].hp > 1){
          if(Math.abs(this.x-enm[i].x) < (l+enm[i].l/2) && Math.abs(this.y-enm[i].y) < (l+enm[i].l/2)){
            hp = 0;
            enm[i].hp = 1;
            sc.score++;
            break judgeHit;
          }
        }
      }
      for(i=0; i<enmNum; i++){// 敵の通常弾
        for(j=0; j<enmBltNum; j++){
          if(enmBlt[i][j].hp > 0){
            if(Math.abs(this.x-enmBlt[i][j].x) < (l+enmBlt[i][j].l) && Math.abs(this.y-enmBlt[i][j].y) < (l+enmBlt[i][j].l)){
              hp = 0;
              enmBlt[i][j].hp = 0;
              break judgeHit;
            }
          }
        }
      }

      //System.out.println(rad);
      //System.out.println(x+","+y);
      //System.out.println(rad);
    }
  }
  public void revive(int x, int y, int rad){// 生きている状態にする
    this.rad = rad;
    this.x = x + (int)((Math.sqrt(2)*ftr.tl+1)*Math.cos(Math.toRadians(rad)));
    this.y = y + (int)((Math.sqrt(2)*ftr.tl+1)*Math.sin(Math.toRadians(rad)));
    hp = 1;
    reflection = 0;
    ftr.sf = false;
  }
  public void set(Obstacle[] obs , int obsNum, Fighter ftr, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){// 情報をセットする
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
  public void reset(){// リセット
    hp = 0;
  }
}
