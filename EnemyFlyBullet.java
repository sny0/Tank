import java.awt.*;

// 敵の打上弾
// FighterFlyBulletと同じところはコメント割愛
public class EnemyFlyBullet{
  int i, j;
  int num;
  int x;
  int y;
  int v = 6;
  int vx;
  int vy;
  int rad = 0;
  int l;
  int timer;
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
  Image img;
  int imgX;
  Image explosionImg;
  int explosionImgX;

  EnemyFlyBullet(Image img, Image explosionImg){
    this.img = img;
    this.explosionImg = explosionImg;
  }

  public void move(Graphics g, StageCreate sc){
    if(hp > 0){
      timer++;
      if(hp == 2){
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
      }else if(hp == 1){
        if(timer < 3) explosionImgX = 0;
        else if(timer < 5) explosionImgX = 1;
        else explosionImgX = 2;
        g.drawImage(explosionImg, x-48, y-48, x+48, y+48, 48*2*explosionImgX, 0, 48*2*explosionImgX+48*2, 48*2, null);
        l = 48*2;
        // g.setColor(Color.pink);
        // g.fillOval(x-l/2, y-l/2, l, l);

        for(i=0; i<obsNum; i++){
          if(obs[i].hp > 0 && obs[i].kind == 1){
            if(Math.abs(this.x-obs[i].x-obs[i].l/2) < (l/2+obs[i].l/2) && Math.abs(this.y-obs[i].y-obs[i].l/2) < (l/2+obs[i].l/2)){
              obs[i].hp = 0;
            }
          }
        }
        if(ftr.hp > 1){
          if(Math.abs(this.x-ftr.x) < (l/2+ftr.l/2) && Math.abs(this.y-ftr.y) < (l/2+ftr.l/2)){
            ftr.hp = 1;
          }
        }
        for(i=0; i<ftrBltNum; i++){
          if(ftrBlt[i].hp > 0){
            if(Math.abs(this.x-ftrBlt[i].x) < (l/2+ftrBlt[i].l/2) && Math.abs(this.y-ftrBlt[i].y) < (l/2+ftrBlt[i].l/2)){
              ftrBlt[i].hp = 0;
            }
          }
        }
        for(i=0; i<ftrBmbNum; i++){
          if(ftrBmb[i].hp == 2){
            if(Math.abs(this.x-ftrBmb[i].x) < (l/2+ftrBmb[i].l/2) && Math.abs(this.y-ftrBmb[i].y) < (l/2+ftrBmb[i].l/2)){
              ftrBmb[i].hp = 1;
              ftrBmb[i].timer = 0;
            }
          }
        }
        for(i=0; i<enmNum; i++){
          if(enm[i].hp > 1){
            if(Math.abs(this.x-enm[i].x) < (l/2+enm[i].l/2) && Math.abs(this.y-enm[i].y) < (l/2+enm[i].l/2)){
              enm[i].hp = 1;
              sc.score++;
            }
          }
          for(j=0; j<enmBltNum; j++){
            if(enmBlt[i][j].hp > 0){
              if(Math.abs(this.x-enmBlt[i][j].x) < (l/2+enmBlt[i][j].l/2) && Math.abs(this.y-enmBlt[i][j].y) < (l/2+enmBlt[i][j].l/2)){
                enmBlt[i][j].hp = 0;
              }
            }
          }
        }
      }
      if(timer > flightTime && hp == 2){
        hp =1;
        timer = 0;
      }
      if(timer > explosionTime && hp == 1){
        hp = 0;
      }
    }
  }
  public void revive(int x, int y, int rad){
    this.rad = rad;
    this.x = x + (int)((Math.sqrt(2)*enm[num].tl+1)*Math.cos(Math.toRadians(rad)));
    this.y = y + (int)((Math.sqrt(2)*enm[num].tl+1)*Math.sin(Math.toRadians(rad)));
    vx = (ftr.x-x)/flightTime;// 自機と敵の座標から速度を割り出す
    vy = (ftr.y-y)/flightTime;
    // vx =(int) (v*(Math.cos(Math.toRadians(rad))));
    // vy =(int) (v*(Math.sin(Math.toRadians(rad))));
    hp = 2;
    timer = 0;
    explosionImgX = 0;
  }
  public void reset(){
    hp = 0;
    timer = 0;
  }
  public void set(Obstacle[] obs , int obsNum, Fighter ftr, FighterBullet[] ftrBlt, int ftrBltNum, FighterBomb[] ftrBmb, int ftrBmbNum, Enemy[] enm, int enmNum, EnemyBullet[][] enmBlt, int enmBltNum){
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
