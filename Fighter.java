import java.awt.*;
import java.awt.geom.AffineTransform;

public class Fighter{// 自機
  int i, j, k;
  private int[] gameW;// 横の壁のx座標
  private int[] gameH;// 縦の壁のy座標
  int startPositionX;// スタート時のx座標
  int startPositionY;// スタート時のy座標
  int startRad;// スタート時の角度
  int x;// x座標
  int y;// 座標
  int px;// 前のx座標
  int py;// 前のy座標
  int[] xPoints;// 機体のの各頂点のx座標
  int[] yPoints;// 機体の各頂点のy座標
  int[] xMarkPoints;
  int[] yMarkPoints;
  int[] xTurretPoints;// 機体の上部分の各頂点のx座標
  int[] yTurretPoints;// 機体の上部分の各頂点のy座標
  int[] xMuzzlePoints;// 機体の砲台の各頂点のx座標
  int[] yMuzzlePoints;// 機体の砲台の各頂点のy座標
  int v = 5;// 機体の速さ
  double vx;// 機体のx軸方向の速さ
  double vy;// 機体のy軸方向の速さ
  int rad = 0;// 機体の角度
  int prad;// 機体の前の角度
  int vrad = 3;// 機体の回転速度
  int turretRad = 0;// 砲台の角度
  int vturretRad = 3;// 砲台の回転速度
  int hp = 0;// hp
  int l = 48;//機体の辺の長さ
  int tl = l/2;// 機体の上部分の辺の長さ
  int bltKind = 0;// 現在の弾
  boolean uf = false;// upキーが押されたかどうか
  boolean df = false;// downキーが押されたかどうか
  boolean lf = false;// leftキーが押されたかどうか
  boolean rf = false;// rightキーが押されたかどうか
  boolean zf = false;// zキーが押されたかどうか
  boolean cf = false;// cキーが押されたかどうか
  boolean vf = false;// vキーが押されたかどうか
  boolean sf = false;// spaceキーが押されたかどうか
  Obstacle[] obs;//障害物
  int obsNum;// 障害物の数
  FighterBullet[] ftrBlt;// 自機の弾
  int ftrBltNum;// 自機の弾の数
  FighterBomb[] ftrBmb;// 自機の爆弾
  int ftrBmbNum;// 自機の爆弾の数
  Enemy enm[];// 敵
  int enmNum;// 敵の数
  EnemyBullet[][] enmBlt;// 敵の弾
  int enmBltNum;// 敵の弾の数
  Image ftrImg;// 自機の画像
  Image explosionImg;// 爆発の画像

  int explosionTimer;// 爆発してら経つフレーム数
  int explosionTime = 30;// 爆発している総フレーム
  int explosionImgX;// 画像を取り込む変位
  Fighter(int startX, int startY, int[] w, int[] h, Image ftrImg, Image explosionImg){// コンストラクタ
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = w[0];
    gameW[1] = w[1];
    gameH[0] = h[0];
    gameH[1] = h[1];
    this.ftrImg = ftrImg;
    this.explosionImg = explosionImg;
    x = startX + l/2;
    y = startY + l/2;
    xPoints = new int[4];
    yPoints = new int[4];
    xMarkPoints = new int[4];
    yMarkPoints = new int[4];
    xTurretPoints = new int[4];
    yTurretPoints = new int[4];
    xMuzzlePoints = new int[4];
    yMuzzlePoints = new int[4];
    updateXPoints();
    updateYPoints();
    updateXMarkPoints();
    updateYMarkPoints();
    updateXTurretPoints();
    updateYTurretPoints();

  }

  public void move(Graphics gc, boolean start){// 自機の移動と描画
    if(hp > 1){// 生きているとき
      if(start){// スタートしているとき
        if(rf && !lf){// 右に回転
          prad = rad;
          rad += vrad;
          turretRad += vrad;
        }
        if(lf && !rf){// 左に回転
          prad = rad;
          rad -= vrad;
          turretRad -= vrad;
        }
        // 速さと自機の角度から進む各ベクトルを計算
        vx = v*(Math.cos(Math.toRadians(rad)));
        vy = v*(Math.sin(Math.toRadians(rad)));
        //System.out.println("aa");
        if(uf && !df){// 前進
          px = x;
          py = y;
          x += vx;
          y += vy;
        }
        if(df && !uf){// 後退
          px = x;
          py = y;
          x -= vx;
          y -= vy;
        }
        // 自機の座標を更新
        updateXPoints();
        updateYPoints();

        // 壁との衝突判定
        for(i=0; i<4; i++){// 横の壁と衝突したとき
          if(xPoints[i] <= gameW[0] || xPoints[i] >= gameW[1]){
            if(xPoints[i] <= gameW[0]){
              xPoints[i] = gameW[0];
              calculateX(i);
              x++;
            }else if(xPoints[i] >= gameW[1]){
              xPoints[i] = gameW[1];
              calculateX(i);
              x--;
            }
            //x = px;
            //rad = prad;
            //System.out.println(i+"  x:"+xPoints[i]);
            updateXPoints();

          }
        }
        for(i=0; i<4; i++){// 縦の壁と衝突したとき
          if(yPoints[i] <= gameH[0] || yPoints[i] >= gameH[1]){
            if(yPoints[i] <= gameH[0]){
              yPoints[i] = gameH[0];
              calculateY(i);
              y++;
            }else if(yPoints[i] >= gameH[1]){
              yPoints[i] = gameH[1];
              //y = py;
              calculateY(i);
              //System.out.println(i+"  y:"+yPoints[i]);
              //rad = prad;
              y--;
            }
            updateYPoints();

          }
        }

        //System.out.prinln("x:  "+x+"  y:  "+y);
        // 障害物との衝突判定
        // アルゴリズム：
        // 機体の各頂点が障害物の中に入ったか調べる→入っていたら入った点の対角線上にある点と障害物の中心の点の距離がx、yどちらが離れているか調べる
        // →より離れている方のベクトルから入ったと判定する→右から入ったか、左から入ったか、上から入ったか、下から入ったかを入った点と障害物の中心点から判定する
        // →入った点が入った面の座標（実際は1px離れている）になるように座標を更新する
        for(i=0; i<obsNum; i++){
          if(obs[i].hp > 0){
            for(j=0; j<4; j++){
              if(xPoints[j] >= obs[i].x && xPoints[j] <= obs[i].x+obs[i].l && yPoints[j] >= obs[i].y && yPoints[j] <= obs[i].y+obs[i].l){// 障害物と衝突しているとき
                int a = j + 2;
                if(a > 3) a -= 4;
                if(Math.abs(obs[i].x+obs[i].l/2-xPoints[a]) > Math.abs(obs[i].y+obs[i].l/2-yPoints[a])){
                  if(x <= obs[i].x+obs[i].l/2 && xPoints[j] > obs[i].x){// 障害物の左側から入って衝突したとき
                    xPoints[j] = obs[i].x;
                    calculateX(j);
                    x--;
                    updateXPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }else if(x >= obs[i].x+obs[i].l/2 && xPoints[j] < obs[i].x+obs[i].l){// 障害物の右側から入って衝突したとき
                    xPoints[j] = obs[i].x+obs[i].l;
                    calculateX(j);
                    x++;
                    updateXPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }
                }else if(Math.abs(obs[i].x+obs[i].l/2-xPoints[a]) < Math.abs(obs[i].y+obs[i].l/2-yPoints[a])){
                  if(y <= obs[i].y+obs[i].l/2 && yPoints[j] > obs[i].y){// 障害物の上側から入って衝突したとき
                    yPoints[j] = obs[i].y;
                    calculateY(j);
                    y--;
                    updateYPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }else if(y >= obs[i].y+obs[i].l/2 && yPoints[j] < obs[i].y+obs[i].l){// 障害物の下側から入って衝突したとき
                    yPoints[j] = obs[i].y+obs[i].l;
                    calculateY(j);
                    y++;
                    updateYPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }
                }
              }
            }
          }
        }
        // 機体の角度を(-180,180)に変換
        while (rad >= 180){
          rad -= 360;
        }
        while (rad <= -180){
          rad += 360;
        }
        // 砲台の回転
        if(cf){
          turretRad += vturretRad;
        }
        if(zf){
          turretRad -= vturretRad;
        }
        // 砲台の角度を(-180,180)に変換
        while (turretRad >= 180){
          turretRad -= 360;
        }
        while (turretRad <= -180){
          turretRad += 360;
        }
        //System.out.println(turretRad);
        //System.out.println(rad);
        //System.out.println("(x,y) = ("+x+","+y+")");
        ///System.out.println("0:"+xPoints[0]+","+yPoints[0]+",1:"+xPoints[1]+","+yPoints[1]+",2:"+xPoints[2]+","+yPoints[2]+",3:"+xPoints[3]+","+yPoints[3]);
        //System.out.println("0:"+xMuzzlePoints[0]+","+yMuzzlePoints[0]+",1:"+xMuzzlePoints[1]+","+yMuzzlePoints[1]+",2:"+xMuzzlePoints[2]+","+yMuzzlePoints[2]+",3:"+xMuzzlePoints[3]+","+yMuzzlePoints[3]);
      }
      // 機体の座標を更新
      updateXPoints();
      updateYPoints();
      updateXMarkPoints();
      updateYMarkPoints();
      updateXTurretPoints();
      updateYTurretPoints();
      // 角度を付けて画像を描画
      Graphics2D g2 = (Graphics2D) gc;// Graphics をGraphics2Dに変換
      AffineTransform at = g2.getTransform();
      at.setToRotation(Math.toRadians(rad), x, y);// 機体の回転の角度と中心を設定
		  g2.setTransform(at);
      g2.drawImage(ftrImg, x-l/2, y-l/2, x+l/2, y+l/2, 0, 0, 48, 48, null);
      at.setToRotation(Math.toRadians(turretRad), x, y);//　砲台の回転の角度と中心を設定
		  g2.setTransform(at);
      // 現在の弾に合わせて描画する絵を変える
      if(bltKind == 0) g2.drawImage(ftrImg, x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 0, 48+12*5, 24, null);// 通常弾の時
      else g2.drawImage(ftrImg, x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 24, 48+12*5, 48, null);// 打上弾の時
      at.setToRotation(Math.toRadians(0));// 角度を0に戻す
      g2.setTransform(at);
      // gc.setColor(Color.black);
      // gc.setColor(new Color(0x00, 0x00, 0x8b));
      // gc.fillPolygon(xPoints, yPoints, 4);
      // gc.setColor(new Color(0x00, 0xbf, 0xff));
      // gc.fillPolygon(xMarkPoints, yMarkPoints, 4);
      // gc.setColor(new Color(0x64, 0x95, 0xed));
      // gc.fillPolygon(xMuzzlePoints, yMuzzlePoints, 4);
      // gc.setColor(new Color(0x00, 0x00, 0xcd));
      // gc.fillPolygon(xTurretPoints, yTurretPoints, 4);

    }else if(hp == 1){// 爆発中
      explosionTimer++;
      if(explosionTimer % 4 == 0) explosionImgX++;
      // タイマーによって描画する絵を変える
      gc.drawImage(explosionImg, x-l/2, y-l/2, x+l/2, y+l/2, 48*explosionImgX, 0, 48*explosionImgX+48, 48, null);
      if(explosionTimer >= explosionTime) hp = 0;// 爆発時間が過ぎたらhpを0に
    }
  }
  private void updateXPoints(){// 機体の各頂点のx座標を更新
    xPoints[0] = (int)(x + l * Math.cos(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    xPoints[1] = (int)(x + l * Math.cos(Math.toRadians(rad+45*3))/ Math.sqrt(2));
    xPoints[2] = (int)(x + l * Math.cos(Math.toRadians(rad+45*5))/ Math.sqrt(2));
    xPoints[3] = (int)(x + l * Math.cos(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }

  private void updateYPoints(){// 機体の各頂点のy座標を更新
    yPoints[0] = (int)(y + l * Math.sin(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    yPoints[1] = (int)(y + l * Math.sin(Math.toRadians(rad+45*3))/ Math.sqrt(2));
    yPoints[2] = (int)(y + l * Math.sin(Math.toRadians(rad+45*5))/ Math.sqrt(2));
    yPoints[3] = (int)(y + l * Math.sin(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }
  /*現在使ていないメソッド*/
  private void updateXMarkPoints(){
    xMarkPoints[0] = (int)(x + l * Math.cos(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    xMarkPoints[1] = (int)(x + l/4 *Math.sqrt(5)*Math.cos(Math.toRadians(rad+60)));
    xMarkPoints[2] = (int)(x + l/4 *Math.sqrt(5)*Math.cos(Math.toRadians(rad+300)));
    xMarkPoints[3] = (int)(x + l * Math.cos(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }
  private void updateYMarkPoints(){
    yMarkPoints[0] = (int)(y + l * Math.sin(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    yMarkPoints[1] = (int)(y + l/4 *Math.sqrt(5)*Math.sin(Math.toRadians(rad+60)));
    yMarkPoints[2] = (int)(y + l/4 *Math.sqrt(5)*Math.sin(Math.toRadians(rad+300)));
    yMarkPoints[3] = (int)(y + l * Math.sin(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }


  private void updateXTurretPoints(){// 砲台の各頂点のx座標を計算
    xTurretPoints[0] = (int)(x + tl * Math.cos(Math.toRadians(turretRad+45*1))/ Math.sqrt(2));
    xTurretPoints[1] = (int)(x + tl * Math.cos(Math.toRadians(turretRad+45*3))/ Math.sqrt(2));
    xTurretPoints[2] = (int)(x + tl * Math.cos(Math.toRadians(turretRad+45*5))/ Math.sqrt(2));
    xTurretPoints[3] = (int)(x + tl * Math.cos(Math.toRadians(turretRad+45*7))/ Math.sqrt(2));
    /*
    xMuzzlePoints[0] = (int)(x + tl/4 * Math.sqrt(5) * Math.cos(Math.toRadians(turretRad+30)));
    xMuzzlePoints[1] = (int)(x + tl/4 * Math.sqrt(5) * Math.cos(Math.toRadians(turretRad-30)));
    xMuzzlePoints[2] = (int)(x + tl * Math.cos(Math.toRadians(turretRad)) + tl/4 * Math.sqrt(5) * Math.cos(Math.toRadians(turretRad-30)));
    xMuzzlePoints[3] = (int)(x + tl * Math.cos(Math.toRadians(turretRad)) + tl/4 * Math.sqrt(5) * Math.cos(Math.toRadians(turretRad+30)));
    */
    xMuzzlePoints[0] = (int)(x+tl/3/Math.sqrt(2)*Math.cos(Math.toRadians(turretRad+90)));
    xMuzzlePoints[1] = (int)(x+tl/3/Math.sqrt(2)*Math.cos(Math.toRadians(turretRad-90)));
    xMuzzlePoints[2] = (int)(x+tl/3/Math.sqrt(2)*Math.cos(Math.toRadians(turretRad-90))+Math.sqrt(2)*tl*Math.cos(Math.toRadians(turretRad)));
    xMuzzlePoints[3] = (int)(x+tl/3/Math.sqrt(2)*Math.cos(Math.toRadians(turretRad+90))+Math.sqrt(2)*tl*Math.cos(Math.toRadians(turretRad)));
  }

  private void updateYTurretPoints(){// 砲台の各頂点のy座標を計算
    yTurretPoints[0] = (int)(y + tl * Math.sin(Math.toRadians(turretRad+45*1))/ Math.sqrt(2));
    yTurretPoints[1] = (int)(y + tl * Math.sin(Math.toRadians(turretRad+45*3))/ Math.sqrt(2));
    yTurretPoints[2] = (int)(y + tl * Math.sin(Math.toRadians(turretRad+45*5))/ Math.sqrt(2));
    yTurretPoints[3] = (int)(y + tl * Math.sin(Math.toRadians(turretRad+45*7))/ Math.sqrt(2));
    /*
    yMuzzlePoints[0] = (int)(y + tl/4 * Math.sqrt(5) * Math.sin(Math.toRadians(turretRad+30)));
    yMuzzlePoints[1] = (int)(y + tl/4 * Math.sqrt(5) * Math.sin(Math.toRadians(turretRad-30)));
    yMuzzlePoints[2] = (int)(y + tl * Math.sin(Math.toRadians(turretRad)) + tl/4 * Math.sqrt(5) * Math.sin(Math.toRadians(turretRad-30)));
    yMuzzlePoints[3] = (int)(y + tl * Math.sin(Math.toRadians(turretRad)) + tl/4 * Math.sqrt(5) * Math.sin(Math.toRadians(turretRad+30)));
    */
    yMuzzlePoints[0] = (int)(y+tl/3/Math.sqrt(2)*Math.sin(Math.toRadians(turretRad+90)));
    yMuzzlePoints[1] = (int)(y+tl/3/Math.sqrt(2)*Math.sin(Math.toRadians(turretRad-90)));
    yMuzzlePoints[2] = (int)(y+tl/3/Math.sqrt(2)*Math.sin(Math.toRadians(turretRad-90))+Math.sqrt(2)*tl*Math.sin(Math.toRadians(turretRad)));
    yMuzzlePoints[3] = (int)(y+tl/3/Math.sqrt(2)*Math.sin(Math.toRadians(turretRad+90))+Math.sqrt(2)*tl*Math.sin(Math.toRadians(turretRad)));
  }

  private void calculateX(int i){// 機体の頂点のx座標からxを求める
    x = (int)(xPoints[i] - l * Math.cos(Math.toRadians(rad+45*(1+2*i)))/ Math.sqrt(2));

  }
  private void calculateY(int i){// 機体の頂点のy座標からyを求める
    y = (int)(yPoints[i] - l * Math.sin(Math.toRadians(rad+45*(1+2*i)))/ Math.sqrt(2));
  }

  public void create(int x, int y, int rad){// 自機を生きてる状態にする
    this.startPositionX = x + l/2;
    this.startPositionY = y + l/2;
    startRad = rad;
    this.x = x + l/2;
    this.y = y + l/2;
    this.rad = rad;
    this.turretRad = rad;
    hp = 2;
    explosionTimer = 0;
    explosionImgX = 0;
  }
  public void recreate(){// 自機を作り直す
    x = startPositionX;
    y = startPositionY;
    rad = startRad;
    turretRad = startRad;
    hp = 2;
    explosionTimer = 0;
    explosionImgX = 0;
  }
  public void set(Obstacle[] obs, int obsNum, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){// 情報をセットする
    this.obs = obs;
    this.obsNum = obsNum;
    this.ftrBlt = ftrBlt;
    this.ftrBltNum = ftrBltNum;
    this.ftrBmb = ftrBmb;
    this.ftrBmbNum = ftrBmbNum;
    this.enm = enm;
    this.enmNum = enmNum;
    this.enmBlt = enmBlt;
    this.enmBltNum = enmBltNum;
  }
  public void reset(){// 自機を死んでいる状態にする
    hp = 0;
  }
}
