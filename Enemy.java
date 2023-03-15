import java.awt.*;
import java.awt.geom.AffineTransform;

// 敵
public class Enemy{
  int i, j, k;
  int num;// 自分のナンバー
  int kind; // 0: normal brawn 1: flybullet orange 2: transparence white 3: strong purple
  private int[] gameW;
  private int[] gameH;
  int x;// 座標
  int y;
  int startPositionX;// スタートじの座標
  int startPositionY;
  int startRad;// スタート時の角度
  boolean survival = false;// 生きていたかどうか
  int px;// 前の座標
  int py;
  int[] xPoints;
  int[] yPoints;
  int[] xTurretPoints;// 機体の上部分の座標
  int[] yTurretPoints;
  int[] xMuzzlePoints;// 砲台の座標
  int[] yMuzzlePoints;
  int v = 5;// 速度
  double vx;// 速度のベクトル
  double vy;
  int rad = 180;// 機体の角度
  int prad;// 前の角度
  int vrad = 3;// 角度の変わる速さ
  int turretRad;// 砲台の角度
  int vturretRad = 3;// 砲台の角度の変わる速さ
  int targetRad;// 自分から見たときの自機の角度
  int drad;// targetRadと砲台の角度の差
  int bltKind;// 現在の弾
  int hp = 0;// hp
  int l = 48;// 機体の辺の長さ
  int tl = l/2;// 砲台の上部分の辺の長さ
  Fighter ftr;// 自機
  boolean uf = false;// 上に進むかどうか
  boolean df = false;// 下に進むがどうか
  boolean lf = false;// 左に回転するどうか
  boolean rf = false;// 右に回転するかどうか
  int utimer = 10;// 上に進むフレーム数
  int dtimer = 10;// 下に進むフレーム数
  int rtimer = 10;// 右に回転するフレーム数
  int ltimer = 10;// 左に大転するフレーム数
  int timer = 0;// タイマー
  Image[] enmImg;
  Image explosionImg;
  Obstacle[] obs;
  int obsNum;
  FighterBullet[] ftrBlt;
  int ftrBltNum;
  FighterBomb[] ftrBmb;
  int ftrBmbNum;
  Enemy enm[];
  int enmNum;
  EnemyBullet[][] enmBlt;
  int enmBltNum;
  EnemyFlyBullet[][] enmFlyBlt;
  int enmFlyBltNum;

  int dx;
  int dy;
  int bigger;

  int[][] waitTime;
  int wait;

  int explosionTimer;// 爆発のタイマー
  int explosionTime = 30;// 爆発している時間
  int explosionImgX;// 爆発の描画する場所
  int skeltonTime = 75;// 消える時間
  int skeltonTimer = 0;// 消える敵のタイマー
  Enemy(int startX, int startY, int[] w, int[] h, int num, Image[] enmImg, Image explosionImg){
    this.num = num;
    this.enmImg = enmImg;
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = w[0];
    gameW[1] = w[1];
    gameH[0] = h[0];
    gameH[1] = h[1];
    this.explosionImg = explosionImg;
    x = startX + l/2;
    y = startY + l/2;
    xPoints = new int[4];
    yPoints = new int[4];
    xTurretPoints = new int[4];
    yTurretPoints = new int[4];
    xMuzzlePoints = new int[4];
    yMuzzlePoints = new int[4];
    waitTime = new int[4][2];
    waitTime[0][0] = 30;
    waitTime[1][0] = 0;
    waitTime[2][0] = 20;
    waitTime[3][0] = 10;
    waitTime[0][1] = 0;
    waitTime[1][1] = 45;
    waitTime[2][1] = 0;
    waitTime[3][1] = 30;
    updateXPoints();
    updateYPoints();
    updateXTurretPoints();
    updateYTurretPoints();
  }

  public void move(Graphics gc, boolean start){// 判定と描画
    if(hp > 1){
      if(start){
        skeltonTimer++;
        if(!uf && !df && !rf && !lf){// 一定の確率で前進後退回転する
          if(Math.random() < 0.2){
            if(Math.random() < 0.25){
              uf = true;
            }else if(Math.random() < 0.25){
              df = true;
            }else if(Math.random() < 0.25){
              rf = true;
            }else{
              lf = true;
            }
          }
        }
        // 速度の成分を計算
        vx = v*(Math.cos(Math.toRadians(rad)));
        vy = v*(Math.sin(Math.toRadians(rad)));
        // 動作
        if(uf){// 前進
          if(timer < utimer){
            px = x;
            py = y;
            x += vx;
            y += vy;
            timer++;
          }else{
            uf = false;
            timer = 0;
          }
        }
        if(df){// 後退
          if(timer < dtimer){
            px = x;
            py = y;
            x -= vx;
            y -= vy;
            timer++;
          }else{
            df = false;
            timer = 0;
          }
        }
        if(rf){// 右回転
          if(timer < rtimer){
            prad = rad;
            rad += vrad;
            timer++;
          }else{
            rf = false;
            timer = 0;
          }
        }
        if(lf){// 左回転
          if(timer < ltimer){
            prad = rad;
            rad -= vrad;
            timer++;
          }else{
            lf = false;
            timer = 0;
          }
        }

        updateXPoints();
        updateYPoints();
        // 壁との衝突判定
        for(i=0; i<4; i++){// 横の壁との衝突判定
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
        for(i=0; i<4; i++){// 縦の壁との衝突判定
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
        // 障害物との衝突判定
        for(i=0; i<obsNum; i++){
          if(obs[i].hp > 0){
            for(j=0; j<4; j++){
              if(xPoints[j] >= obs[i].x && xPoints[j] <= obs[i].x+obs[i].l && yPoints[j] >= obs[i].y && yPoints[j] <= obs[i].y+obs[i].l){
                int a = j + 2;
                if(a > 3) a -= 4;
                if(Math.abs(obs[i].x+obs[i].l/2-xPoints[a]) > Math.abs(obs[i].y+obs[i].l/2-yPoints[a])){
                  if(x <= obs[i].x+obs[i].l/2 && xPoints[j] > obs[i].x){
                    xPoints[j] = obs[i].x;
                    calculateX(j);
                    x--;
                    updateXPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }else if(x >= obs[i].x+obs[i].l/2 && xPoints[j] < obs[i].x+obs[i].l){
                    xPoints[j] = obs[i].x+obs[i].l;
                    calculateX(j);
                    x++;
                    updateXPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }
                }else if(Math.abs(obs[i].x+obs[i].l/2-xPoints[a]) < Math.abs(obs[i].y+obs[i].l/2-yPoints[a])){
                  if(y <= obs[i].y+obs[i].l/2 && yPoints[j] > obs[i].y){
                    yPoints[j] = obs[i].y;
                    calculateY(j);
                    y--;
                    updateYPoints();
                    // gc.setColor(Color.red);
                    // gc.fillOval(xPoints[j], yPoints[j], 10, 10);
                  }else if(y >= obs[i].y+obs[i].l/2 && yPoints[j] < obs[i].y+obs[i].l){
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
        // 敵機から見た自機の角度を求める
        targetRad = (int)(Math.atan2(ftr.y-y, ftr.x-x)/Math.PI*180);

        drad = targetRad - turretRad;
        // 砲台の角度が徐々に自機の方向へ向くようにする
        // 砲台の角度と自分から見た自機の角度からどの方向に回転したら速いか調べる
        if(Math.abs(drad) < vturretRad || 360 - Math.abs(drad) < vturretRad){
          turretRad = targetRad;
        }else if(drad >= 0 && drad <= 180){
          turretRad += vturretRad;
          while (turretRad > 180){
            turretRad -= 360;
          }
          while (turretRad <= -180){
            turretRad += 360;
          }

        }else if(drad > 180){
          turretRad -= vturretRad;
          while (turretRad > 180){
            turretRad -= 360;
          }
          while (turretRad <= -180){
            turretRad += 360;
          }

        }else if(drad < 0 && drad > -180){
          turretRad -= vturretRad;
          while (turretRad > 180){
            turretRad -= 360;
          }
          while (turretRad <= -180){
            turretRad += 360;
          }

        }else if(drad < -180){
          turretRad += vturretRad;
          while (turretRad > 180){
            turretRad -= 360;
          }
          while (turretRad <= -180){
            turretRad += 360;
          }

        }

        // 敵機の角度の調整
        while (rad >= 180){
          rad -= 360;
        }
        while (rad <= -180){
          rad += 360;
        }

        //System.out.println(rad);
        //System.out.println("target:"+targetRad+"  turretRad:"+turretRad);
        //System.out.println("uf:"+uf+"  df:"+df+"  rf:"+rf+"  lf:"+lf);
        //System.out.println(rad);
        //System.out.println("(x,y) = ("+x+","+y+")");
        ///System.out.println("0:"+xPoints[0]+","+yPoints[0]+",1:"+xPoints[1]+","+yPoints[1]+",2:"+xPoints[2]+","+yPoints[2]+",3:"+xPoints[3]+","+yPoints[3]);
        //System.out.println("0:"+xMuzzlePoints[0]+","+yMuzzlePoints[0]+",1:"+xMuzzlePoints[1]+","+yMuzzlePoints[1]+",2:"+xMuzzlePoints[2]+","+yMuzzlePoints[2]+",3:"+xMuzzlePoints[3]+","+yMuzzlePoints[3]);
      }
      // 座標の更新
      updateXPoints();
      updateYPoints();
      updateXTurretPoints();
      updateYTurretPoints();
      //描画
      Graphics2D g2 = (Graphics2D) gc;
      AffineTransform at = g2.getTransform();
      if(kind == 0){// 緑の敵のとき
        at.setToRotation(Math.toRadians(rad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        g2.drawImage(enmImg[kind], x-l/2, y-l/2, x+l/2, y+l/2, 0, 0, 48, 48, null);// 機体の描画
        at.setToRotation(Math.toRadians(turretRad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        // 砲台の描画
        if(bltKind == 0) g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 0, 48+12*5, 24, null);// 通常弾の時の砲台の描画
        else g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 24, 48+12*5, 48, null);// 打上弾の時の砲台の描画
        at.setToRotation(Math.toRadians(0));// 描画角度をリセット
        g2.setTransform(at);
        // gc.setColor(new Color(00, 139, 139));
        // gc.fillPolygon(xPoints, yPoints, 4);
        // gc.setColor(Color.black);
        // gc.drawLine(xPoints[0], yPoints[0], xPoints[3], yPoints[3]);
        // gc.setColor(new Color(47, 79, 79));
        // gc.fillPolygon(xMuzzlePoints, yMuzzlePoints, 4);
        // gc.setColor(new Color(34, 139, 34));
        // gc.fillPolygon(xTurretPoints, yTurretPoints, 4);
      }else if(kind == 1){// 茶色の敵の時
        at.setToRotation(Math.toRadians(rad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        g2.drawImage(enmImg[kind], x-l/2, y-l/2, x+l/2, y+l/2, 0, 0, 48, 48, null);
        at.setToRotation(Math.toRadians(turretRad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        if(bltKind == 0) g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 0, 48+12*5, 24, null);// 通常弾の時の砲台の描画
        else g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 24, 48+12*5, 48, null);// 打上弾の時の砲台の描画
        at.setToRotation(Math.toRadians(0));// 描画角度をリセット
        g2.setTransform(at);
        // gc.setColor(new Color(0xd2, 0x69, 0x1e));
        // gc.fillPolygon(xPoints, yPoints, 4);
        // gc.setColor(Color.black);
        // gc.drawLine(xPoints[0], yPoints[0], xPoints[3], yPoints[3]);
        // gc.setColor(new Color(0xb8, 0x86, 0x0b));
        // gc.fillPolygon(xMuzzlePoints, yMuzzlePoints, 4);
        // gc.setColor(new Color(0xff, 0xa5, 0x00));
        // gc.fillPolygon(xTurretPoints, yTurretPoints, 4);
      }else if(kind == 2){// 白の敵の時
        if(skeltonTimer <= 0 || skeltonTimer > skeltonTime){// 見える時間の時
          at.setToRotation(Math.toRadians(rad), x, y);// 回転の角度と回転の中心の設定
    		  g2.setTransform(at);
          g2.drawImage(enmImg[kind], x-l/2, y-l/2, x+l/2, y+l/2, 0, 0, 48, 48, null);
          at.setToRotation(Math.toRadians(turretRad), x, y);// 回転の角度と回転の中心の設定
    		  g2.setTransform(at);
          if(bltKind == 0) g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 0, 48+12*5, 24, null);// 通常弾の時の砲台の描画
          else g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 24, 48+12*5, 48, null);// 打上弾の時の砲台の描画
          at.setToRotation(Math.toRadians(0));// 描画角度をリセット
          g2.setTransform(at);
          // gc.setColor(new Color(0xf8, 0xf8, 0xff));
          // gc.fillPolygon(xPoints, yPoints, 4);
          // gc.setColor(Color.black);
          // gc.drawLine(xPoints[0], yPoints[0], xPoints[3], yPoints[3]);
          // gc.setColor(new Color(0xff, 0xe4, 0xe1));
          // gc.fillPolygon(xMuzzlePoints, yMuzzlePoints, 4);
          // gc.setColor(Color.black);
          // gc.drawPolygon(xMuzzlePoints, yMuzzlePoints, 4);
          // gc.setColor(new Color(0xff, 0xf0, 0xf5));
          // gc.fillPolygon(xTurretPoints, yTurretPoints, 4);
          // gc.setColor(Color.black);
          // gc.drawPolygon(xTurretPoints, yTurretPoints, 4);
        }
      }else if(kind == 3){// 紫の敵の時
        at.setToRotation(Math.toRadians(rad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        g2.drawImage(enmImg[kind], x-l/2, y-l/2, x+l/2, y+l/2, 0, 0, 48, 48, null);
        at.setToRotation(Math.toRadians(turretRad), x, y);// 回転の角度と回転の中心の設定
  		  g2.setTransform(at);
        if(bltKind == 0) g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 0, 48+12*5, 24, null);// 通常弾の時の砲台の描画
        else g2.drawImage(enmImg[kind], x-tl/2, y-tl/2, x+tl/2*4, y+tl/2, 48, 24, 48+12*5, 48, null);// 打上弾の時の砲台の描画
        at.setToRotation(Math.toRadians(0));// 描画角度をリセット
        g2.setTransform(at);
        // gc.setColor(new Color(0x6a, 0x5a, 0xcd));
        // gc.fillPolygon(xPoints, yPoints, 4);
        // gc.setColor(Color.black);
        // gc.drawLine(xPoints[0], yPoints[0], xPoints[3], yPoints[3]);
        // gc.setColor(new Color(0x8b, 0x00, 0x8b));
        // gc.fillPolygon(xMuzzlePoints, yMuzzlePoints, 4);
        // gc.setColor(new Color(0x48, 0x3d, 0x8b));
        // gc.fillPolygon(xTurretPoints, yTurretPoints, 4);
      }
      //System.out.println(skeltonTimer);
      if(skeltonTimer >= 90) skeltonTimer = 0;// 透明のタイマーのリセット
    }else if(hp == 1){// 撃破されたときの描画
      explosionTimer++;
      if(explosionTimer % 4 == 0) explosionImgX++;
      gc.drawImage(explosionImg, x-l/2, y-l/2, x+l/2, y+l/2, 48*explosionImgX, 0, 48*explosionImgX+48, 48, null);
      if(explosionTimer >= explosionTime) hp = 0;
    }
  }
  private void updateXPoints(){// 機体の正方形の各点のx座標を計算
    xPoints[0] = (int)(x + l * Math.cos(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    xPoints[1] = (int)(x + l * Math.cos(Math.toRadians(rad+45*3))/ Math.sqrt(2));
    xPoints[2] = (int)(x + l * Math.cos(Math.toRadians(rad+45*5))/ Math.sqrt(2));
    xPoints[3] = (int)(x + l * Math.cos(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }

  private void updateYPoints(){// 機体の正方形の各点のy座標を計算
    yPoints[0] = (int)(y + l * Math.sin(Math.toRadians(rad+45*1))/ Math.sqrt(2));
    yPoints[1] = (int)(y + l * Math.sin(Math.toRadians(rad+45*3))/ Math.sqrt(2));
    yPoints[2] = (int)(y + l * Math.sin(Math.toRadians(rad+45*5))/ Math.sqrt(2));
    yPoints[3] = (int)(y + l * Math.sin(Math.toRadians(rad+45*7))/ Math.sqrt(2));
  }

  private void updateXTurretPoints(){// 砲台の長方形の各点のx座標を計算
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

  private void updateYTurretPoints(){// 砲台の長方形の各点のy座標を計算
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
  private void calculateX(int i){// 機体の点から機体の中心のx座標を求めて変える
    x = (int)(xPoints[i] - l * Math.cos(Math.toRadians(rad+45*(1+2*i)))/ Math.sqrt(2));

  }
  private void calculateY(int i){// 機体の点から機体の中心のy座標を求めて変える
    y = (int)(yPoints[i] - l * Math.sin(Math.toRadians(rad+45*(1+2*i)))/ Math.sqrt(2));
  }

  // 弾の発射をするか判定する
  public int judgeFire(Graphics g){//return; 0: no 1:normalBlt 2: flyBlt
    if(hp > 1){
      wait++;
      if(kind == 0 || kind == 2){
        if(judgeNormalBulletFire(g)) return 1;
        else return 0;
      }else if(kind == 1){
        if(judgeFlyBulletFire(g)) return 2;
        else return 0;
      }else if(kind == 3){
        if(judgeNormalBulletFire(g)) return 3;
        else if(judgeFlyBulletFire(g)) return 2;
        else return 0;
      }
    }
    return 0;
  }
  public void makeBullet(int n){// 弾を発射する
    if(hp > 1){
      if(n == 1){
        if(Math.random() < 0.05){
          for(i=0; i<enmBltNum; i++){
            if(enmBlt[num][i].hp == 0){
              enmBlt[num][i].revive(x, y, turretRad, 0);
              wait = 0;
              break;
            }
          }
        }
      }else if(n == 2){
        if(Math.random() < 0.05){
          for(i=0; i<enmFlyBltNum; i++){
            if(enmFlyBlt[num][i].hp == 0){
              enmFlyBlt[num][i].revive(x, y, turretRad);
              wait = 0;
              break;
            }
          }
        }
      }else if(n == 3){
        if(Math.random() < 0.05){
          for(i=0; i<enmBltNum; i++){
            if(enmBlt[num][i].hp == 0){
              enmBlt[num][i].revive(x, y, turretRad, 1);
              wait = 0;
              break;
            }
          }
        }
      }
    }
  }
  public boolean judgeNormalBulletFire(Graphics g){// 通常弾の発射をするか判定する
    if(waitTime[kind][0] > wait) return false;
    bltKind = 0;
    dx = 2*(ftr.x-x)/l;
    dy = 2*(ftr.y-y)/l;
    bltKind = 0;
    if(Math.abs(dx) >= Math.abs(dy)){
      bigger = Math.abs(dx);
    }else{
      bigger = Math.abs(dy);
    }
    if(bigger == 0) bigger = 1;
    for(i=1; i<bigger+1; i++){
      // g.setColor(Color.red);
      // g.fillOval(x+(ftr.x-x)/bigger*i, y+(ftr.y-y)/bigger*i, 3, 3);
      for(k=0; k<obsNum; k++){
        if(obs[k].hp > 0){
          if(obs[k].x <= x+(ftr.x-x)/bigger*i && obs[k].x+obs[k].l >= x+(ftr.x-x)/bigger*i && obs[k].y <= y+(ftr.y-y)/bigger*i && obs[k].y+obs[k].l >= y+(ftr.y-y)/bigger*i){
            //System.out.println("false");
            return false;
          }
        }
      }
      for(k=0; k<enmNum; k++){
        if(k != num && enm[k].hp > 0){
          if(enm[k].x-l/2 <= x+(ftr.x-x)/bigger*i && enm[k].x+l/2 >= x+(ftr.x-x)/bigger*i && enm[k].y-l/2 < y+(ftr.y-y)/bigger*i && enm[k].y+l/2 >= y+(ftr.y-y)/bigger*i){
            //System.out.println("false");
            return false;
          }
        }
      }
    }
    return true;
  }
  public boolean judgeFlyBulletFire(Graphics g){// 打上弾の発射をするか判定する
    if(waitTime[kind][1] > wait) return false;
    bltKind = 1;
    // for(i=0; i<20; i++){
    //   g.setColor(Color.blue);
    //   g.fillOval(ftr.x+(int)(48*Math.cos(2*Math.PI*(i+1)/20)), ftr.y+(int)(48*Math.sin(2*Math.PI*(i+1)/20)), 3, 3);
    // }
    for(i=0; i<enmNum; i++){
      if((ftr.x-enm[i].x)*(ftr.x-enm[i].x) + (ftr.y-enm[i].y)*(ftr.y-enm[i].y) <= (48 + enm[i].tl)*(48 + enm[i].tl)) return false;
    }
    return true;
  }
  public void create(int x, int y, int rad, int kind){// 機体に命を与える
    this.startPositionX = x + l/2;
    this.startPositionY = y + l/2;
    startRad = rad;
    this.x = x + l/2;
    this.y = y + l/2;
    this.rad = rad;
    this.turretRad = rad;
    hp = 2;
    this.kind = kind;
    skeltonTimer = 0;
    if(kind == 0){
      v = 5;
      vrad = 3;
    }else if(kind == 1){
      v = 0;
      vrad = 0;
    }else if(kind == 2){
      v = 5;
      vrad = 3;
    }else if(kind == 3){
      v = 7;
      vrad = 5;
    }
    explosionTimer = 0;
    explosionImgX = 0;
    bltKind = 0;
  }
  public void recreate(){// ロード
    if(survival){
      x = startPositionX;
      y = startPositionY;
      rad = startRad;
      turretRad = startRad;
      hp = 2;
      survival = false;
      wait = 0;
      skeltonTimer = 0;
      explosionTimer = 0;
      explosionImgX = 0;
      bltKind = 0;
    }
  }
  public void set(Fighter ftr, Obstacle[] obs, int obsNum, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum, EnemyFlyBullet[][] enmFlyBlt, int enmFlyBltNum){// 情報を渡す
    this.ftr = ftr;
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
    this.enmFlyBlt = enmFlyBlt;
    this.enmFlyBltNum = enmFlyBltNum;
  }
  public void reset(){// リセット
    if(hp > 1) survival = true;
    hp = 0;
  }
  public void allReset(){// 完全にリセット
    survival = false;
    hp = 0;
  }

}
