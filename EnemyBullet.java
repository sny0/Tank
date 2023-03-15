import java.awt.*;
import java.awt.geom.AffineTransform;

// 敵の通常弾
// FighterBulletと同じところはコメント割愛
public class EnemyBullet{
  int i, j;
  int numE;// 自分を生み出した敵
  int numB;// 自分のナンバー
  private int[] gameW;
  private int[] gameH;
  int bltKind; //0: normal 1: fast
  int x;
  int y;
  int px;
  int py;
  int v;
  int vx;
  int vy;
  int rad = 0;
  int l = 5;
  int hp;
  int reflection = 0;
  int timer = 0;
  Fighter ftr;
  Enemy[] enm;
  Obstacle[] obs;
  int obsNum;
  FighterBullet[] ftrBlt;
  int ftrBltNum;
  FighterBomb[] ftrBmb;
  int ftrBmbNum;
  int enmNum;
  EnemyBullet[][] enmBlt;
  int enmBltNum;
  Image[] bltImg;
  EnemyBullet(int[] w, int[] h, int numE, int numB, Image[] bltImg){
    this.bltImg = bltImg;
    this.numE = numE;
    this.numB = numB;
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = w[0];
    gameW[1] = w[1];
    gameH[0] = h[0];
    gameH[1] = h[1];
  }

  public void move(Graphics g, StageCreate sc){// 判定と描画
    judgeHit: if(hp > 0){
      if(bltKind == 0){
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = g2.getTransform();
        at.setToRotation(Math.toRadians(rad), x, y);
        g2.setTransform(at);
        g.drawImage(bltImg[0], x-l, y-l, null);
        at.setToRotation(Math.toRadians(0));
        g2.setTransform(at);
        // g.setColor(Color.orange);
        // g.fillOval(x-l, y-l, 2*l, 2*l);
      }else if(bltKind == 1){
        timer++;
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = g2.getTransform();
        at.setToRotation(Math.toRadians(rad), x, y);
        g2.setTransform(at);
        g.drawImage(bltImg[1], x-l, y-l, x+l, y+l, 10*(timer%2), 0, 10+10*(timer%2), 10, null);
        at.setToRotation(Math.toRadians(0));
        g2.setTransform(at);
        // g.setColor(new Color(0x00, 0xce, 0xd1));
        // g.fillOval(x-l, y-l, 2*l, 2*l);
      }
      vx =(int) (v*(Math.cos(Math.toRadians(rad))));
      vy =(int) (v*(Math.sin(Math.toRadians(rad))));

      //System.out.println(vx+","+vy);
      px = x;
      py = y;
      x += vx;
      y += vy;

      if(x-l <= gameW[0] || x+l >= gameW[1]){
        rad = 180 - rad;
        reflection ++;
        if(reflection == 2) hp = 0;
        if(x-l <= gameW[0]) x = gameW[0]+l+1;
        if(x+l >= gameW[1]) x = gameW[1]-l-1;

      }
      if(y-l <= gameH[0] || y+l >= gameH[1]){
        rad = -1 * rad;
        reflection ++;
        if(reflection == 2) hp = 0;
        if(y-l <= gameH[0]) y = gameH[0]+l+1;
        if(y+l >= gameH[1]) y = gameH[1]-l-1;

      }
      for(int j=0; j<obsNum; j++){
        if(obs[j].hp > 0){
          if(x >= obs[j].x && x <= obs[j].x+48 && y >= obs[j].y && y <= obs[j].y+48){
            if(px <= obs[j].x || px >= obs[j].x+48){
              rad = 180 - rad;
              if(px < obs[j].x) x = obs[j].x-l-1;
              if(px > obs[j].x+48) x = obs[j].x+48+l+1;
            }
            if(py <= obs[j].y || py >= obs[j].y+48){
              rad = -1 * rad;
              if(py < obs[j].y) y = obs[j].y-l-1;
              if(py > obs[j].y+48) y = obs[j].y+48+l+1;
            }
            reflection ++;
            if(reflection == 2) hp = 0;
          }
        }
      }
      while (rad >= 180){
        rad -= 360;
      }
      while (rad <= -180){
        rad += 360;
      }
      if(ftr.hp > 1){
        if(Math.abs(this.x-ftr.x) < (l+ftr.l/2) && Math.abs(this.y-ftr.y) < (l+ftr.l/2)){
          hp = 0;
          ftr.hp = 1;
          break judgeHit;
        }
      }
      for(i=0; i<ftrBltNum; i++){
        if(ftrBlt[i].hp > 0){
          if(Math.abs(this.x-ftrBlt[i].x) < (l+ftrBlt[i].l) && Math.abs(this.y-ftrBlt[i].y) < (l+ftrBlt[i].l)){
            hp = 0;
            ftrBlt[i].hp = 0;
            break judgeHit;
          }
        }
      }
      for(i=0; i<enmNum; i++){
        if(enm[i].hp > 1 && reflection != 0){
          if(Math.abs(this.x-enm[i].x) < (l+enm[i].l/2) && Math.abs(this.y-enm[i].y) < (l+enm[i].l/2)){
            hp = 0;
            enm[i].hp = 1;
            sc.score++;
            break judgeHit;
          }
        }
      }
      for(i=0; i<enmNum; i++){
        for(j=0; j<enmBltNum; j++){
          if(!(i == numE && j == numB) && enmBlt[i][j].hp > 0){
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
  public void revive(int x, int y, int rad, int bltKind){
    this.bltKind = bltKind;
    this.rad = rad;
    this.x = x + (int)((Math.sqrt(2)*enm[numE].tl+1)*Math.cos(Math.toRadians(rad)));
    this.y = y + (int)((Math.sqrt(2)*enm[numE].tl+1)*Math.sin(Math.toRadians(rad)));
    hp = 1;
    if(bltKind == 0){
      reflection = 0;
      v = 5;
    }
    if(bltKind == 1){
      reflection = 1;
      v = 10;
    }
    timer = 0;
  }
  public void set(Obstacle[] obs, int obsNum, Fighter ftr, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){
    this.obs = obs;
    this.obsNum = obsNum;
    this.ftr = ftr;
    this.enm = enm;
    this.ftrBlt = ftrBlt;
    this.ftrBltNum = ftrBltNum;
    this.ftrBmb = ftrBmb;
    this.ftrBmbNum = ftrBmbNum;
    this.enmNum = enmNum;
    this.enmBlt = enmBlt;
    this.enmBltNum = enmBltNum;
  }
  public void reset(){
    hp = 0;
  }
}
