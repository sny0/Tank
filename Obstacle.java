import java.awt.*;
// 障害物
public class Obstacle{
  Image[] obstacleImg;
  int kind; // 1:Destroyable, 0: Not Destroyable
  int x;// x座標
  int y;// y座標
  int startPositionX;// 開始地点
  int startPositionY;
  boolean survival = false;// 生きているかどうか
  int l = 48;// 辺の長さ
  int hp = 0;//can see or not see
  Obstacle(int x, int y, Image[] img){
    this.x = x;
    this.y = y;
    obstacleImg = img;
  }

  public void move(Graphics g){// 描画
    if(hp == 1){// 生きているとき
      if(kind == 0){// 壊せる障害物のとき
        // g.setColor(Color.gray);
        // g.fillRect(x, y, l, l);
        g.drawImage(obstacleImg[0], x, y, null);
      }else if(kind == 1){// 壊せない障害物のとき
        // g.setColor(Color.yellow);
        // g.fillRect(x, y, l, l);
        // g.setColor(Color.black);
        // g.drawRect(x, y, l, l);
        g.drawImage(obstacleImg[1], x, y, null);
      }
    }
  }

  public void create(int x, int y, int kind){// 生きている状態にする
    startPositionX = x;
    startPositionY = y;
    this.x = x;
    this.y = y;
    this.kind = kind;
    hp = 1;
  }
  public void recreate(){// 障害物を作り直す
    if(survival){
      x = startPositionX;
      y = startPositionY;
      hp = 1;
      survival = false;
    }
  }
  public void reset(){// リセット
    if(hp > 0) survival = true;
    hp = 0;
  }
  public void allReset(){// 完全にリセット
    survival = false;
    hp = 0;
  }
}
