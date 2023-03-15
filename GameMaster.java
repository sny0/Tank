import java.awt.*;
import java.awt.event.*;

// リスナーや表示画面について
public class GameMaster extends Canvas implements KeyListener{
  boolean start = true;// スタートしたかどうか
  int timer = 0;// タイマー
  Image buf;// バッファ
  Image fieldImg;// フィールドの絵
  Image remainImg;// 残機の文字
  Image stageImg;// ステージの文字
  Image scoreImg;// スコアの文字
  Image bulletImg;// 現在の弾の文字
  Image titleImg;// タイトルの文字
  Image pleaseImg;// please…の文字
  Image resultImg;// リザルトの文字
  Image congImg;// おめでとうの文字
  Image[] bulletKindImg;//弾の絵
  Image[] startImg;// スタートの文字
  Image numbersImg;// 数字
  int startImgNum = 0;
  Graphics buf_gc;// バッファのペン
  Dimension d;
  int i;
  TankGame tg;
  StageCreate sc;
  private int imgW, imgH;
  private int mode = -1; // モード
  private int pmode;// 前のモード
  private int[] gameW; //ゲーム画面の横
  private int[] gameH; //ゲーム画面の縦

  //コンストラクタ
  GameMaster(int imgW, int imgH, TankGame tg){
    this.imgW = imgW;
    this.imgH = imgH;
    this.tg = tg;
    gameW = new int[2];
    gameH = new int[2];
    gameW[0] = 64;
    gameW[1] = imgW-64;
    gameH[0] = 64;
    gameH[1] = imgH-64;
    setSize(imgW, imgH);
    // イラストを読み込む
    fieldImg = this.getToolkit().getImage("img/illust/Field.png");
    remainImg = this.getToolkit().getImage("img/font/Remain_IP.png");
    stageImg = this.getToolkit().getImage("img/font/Stage_IP.png");
    scoreImg = this.getToolkit().getImage("img/font/Score_IP.png");
    bulletImg = this.getToolkit().getImage("img/font/Bullet_IP.png");
    titleImg = this.getToolkit().getImage("img/font/TITLE.png");
    pleaseImg = this.getToolkit().getImage("img/font/Please.png");
    bulletKindImg = new Image[2];
    bulletKindImg[0] = this.getToolkit().getImage("img/font/Normal_IP.png");
    bulletKindImg[1] = this.getToolkit().getImage("img/font/Fly_IP.png");
    resultImg = this.getToolkit().getImage("img/font/Result.png");
    congImg = this.getToolkit().getImage("img/font/Cong.png");
    startImg = new Image[4];
    startImg[0] = this.getToolkit().getImage("img/font/3_144_IP.png");
    startImg[1] = this.getToolkit().getImage("img/font/2_144_IP.png");
    startImg[2] = this.getToolkit().getImage("img/font/1_144_IP.png");
    startImg[3] = this.getToolkit().getImage("img/font/FIGHT_720_IP.png");
    numbersImg = this.getToolkit().getImage("img/font/Number_IP.png");

    sc = new StageCreate(gameW, gameH);
    addKeyListener(this);
  }

  public void addNotify(){
    super.addNotify();
    buf = createImage(imgW, imgH);
    buf_gc = buf.getGraphics();
  }

  public void paint(Graphics g){//移動と描画
    //System.out.println(sc.stage);
    switch(mode){
      case -3: //result
      buf_gc.setColor(Color.cyan);
      buf_gc.fillRect(0, 0, imgW, imgH);
      buf_gc.drawImage(resultImg, imgW/2-144*3, imgH/4, null);
      buf_gc.drawImage(stageImg, imgW/2-48*2,  imgH/3*2, null);
      drawNumber(imgW/2+48, imgH/3*2, sc.stage, buf_gc);
      buf_gc.drawImage(scoreImg, imgW/2-48*2, imgH/3*2+48*3/2, null);
      drawNumber(imgW/2+48, imgH/3*2+48*3/2, sc.score, buf_gc);
      if(sc.remain > 0) buf_gc.drawImage(congImg, imgW/2-200, imgH/2, null);
      // buf_gc.setColor(Color.green);
      // buf_gc.drawString(" == GAME OVER == ", gameW[1]/2-80, gameH[1]/2-20);
      // buf_gc.drawString(" STAGE "+sc.stage, gameW[1]/2-80, gameH[1]/2);
      // buf_gc.drawString(" SCORE "+sc.score, gameW[1]/2-80, gameH[1]/2+20);
      // buf_gc.drawString("HIT SPACE BAR TO START GAME", gameW[1]/2-80, gameH[1]/2+60);
      break;
      case -2: //pause

      break;
      case -1: //stage start
      buf_gc.setColor(Color.green);
      buf_gc.fillRect(0, 0, imgW, imgH);
      buf_gc.drawImage(titleImg, imgW/2-240*2, imgH/3*1-240/2, null);
      buf_gc.drawImage(pleaseImg, imgW/2-552/2, imgH/3*2, null);
      // buf_gc.setColor(Color.green);
      // buf_gc.drawString(" == TANK GAME TITLE == ", gameW[1]/2-80, gameH[1]/2-20);
      // buf_gc.drawString("HIT SPACE BAR TO START GAME", gameW[1]/2-80, gameH[1]/2+20);
      
      // リセット
      sc.clear = true;
      start = false;
      sc.stage = 0;
      sc.remain = 20;
      sc.score = 0;
      break;

      case 1: //stage1
      if(sc.clear){// クリアしたとき
        if(sc.stage == 8){// 最終ステージクリア
          mode = -3;// リザルト画面に
          break;
        }
        sc.stage++;// ステージを1上げる
        sc.allReset();// リセット
        sc.setStage();// ステージを読みこむ
        // リセット
        sc.clear = false;
        start = false;
        startImgNum = 0;
        timer = 0;
      }
      if(sc.ftr.hp == 0 && sc.remain > 1){// やられたとき
        sc.remain--;// 残機を減らす
        sc.reset();// リセット
        sc.reload();// ステージを読みこむ
        // リセット
        start = false;
        startImgNum = 0;
        timer = 0;
      }else if(sc.ftr.hp == 0 && sc.remain == 1){// 残機がなくなった時
        sc.remain = 0;
        mode = -3;// リザルト画面へ
      }
      // ステージの描画
      buf_gc.setColor(new Color(165, 42, 42));
      buf_gc.fillRect(0, 0, imgW, imgH);
      // buf_gc.setColor(new Color(255, 239, 213));
      // buf_gc.fillRect(gameW[0], gameH[0], gameW[1]-64, gameH[1]-64);
      buf_gc.drawImage(fieldImg, 64, 64, null);
      // buf_gc.setColor(Color.green);
      // buf_gc.drawString(" REMAIN :", 48, 48);
      buf_gc.drawImage(remainImg, 48, (64-48)/2, null);
      drawNumber(48+48*7/2, (64-48)/2, sc.remain, buf_gc);
      // buf_gc.drawString(" STAGE ", 48*10, 48);
      buf_gc.drawImage(stageImg, imgW/2-48*2, (64-48)/2, null);
      drawNumber(imgW/2+48, (64-48)/2, sc.stage, buf_gc);
      // buf_gc.drawString(" SCORE ", 48*20, 48);
      buf_gc.drawImage(scoreImg, imgW-48-48*4, (64-48)/2, null);
      drawNumber(imgW-48-48, (64-48)/2, sc.score, buf_gc);
      buf_gc.drawImage(bulletImg, 48+(imgW-48*2)/3*2, 64+48*15+(64-48)/2, null);
      buf_gc.drawImage(bulletKindImg[sc.ftr.bltKind], 48+(imgW-48*2)/3*2+48*7/2, 64+48*15+(64-48)/2, null);
      sc.move(buf_gc, start);
      //ftr.move(buf_gc);
      if(startImgNum < 4 && timer >= 30){// スタートのカウントダウンの数字を1秒毎に変化
        timer = 0;
        startImgNum++;
      }
      if(!start){
        if(startImgNum < 3){
          buf_gc.drawImage(startImg[startImgNum], imgW/2 - 75, imgH/2 - 75, this);// スタート時のカウントダウンを描画
        }
      }
      if(startImgNum == 3){
        start = true;
        buf_gc.drawImage(startImg[startImgNum], imgW/2 - 360, imgH/2 -75, this);// fightを描画
      }
      if(startImgNum < 4){
        timer++;
      }
    }
    //System.out.println(start);
    g.drawImage(buf, 0, 0, this); // バッファを本物の画用紙に描く
  }



  public void update(Graphics gc){
    paint(gc);
  }


  public void keyTyped(KeyEvent ke){

  }

  public void keyPressed(KeyEvent ke){// キーが押されたとき
    int cd = ke.getKeyCode();
    switch(cd){
      case KeyEvent.VK_ESCAPE://Escapeが押されたとき
      System.out.println("Pressed esc key");
      tg.dispose(); // ウィンドウを閉じる
      System.exit(0); // ゲームを終了する
      break;
      case KeyEvent.VK_Q://Qが押されたとき
      sc.remain = 99;
      break;
      case KeyEvent.VK_LEFT://左矢印が押されたとき
      sc.ftr.lf = true;
      break;
      case KeyEvent.VK_RIGHT://右矢印が押されたとき
      sc.ftr.rf = true;
      break;
      case KeyEvent.VK_UP://上矢印が押されたとき
      sc.ftr.uf = true;
      break;
      case KeyEvent.VK_DOWN://下矢印が押されたとき
      sc.ftr.df = true;
      break;
      case KeyEvent.VK_Z://Zが押されたとき
      sc.ftr.zf = true;
      break;
      case KeyEvent.VK_C://Cが押されたとき
      sc.ftr.cf = true;
      break;
      case KeyEvent.VK_X://Xが押されたとき
      if(sc.ftr.bltKind == 0) sc.ftr.bltKind = 1;
      else sc.ftr.bltKind = 0;
      break;
      case KeyEvent.VK_SPACE://Spaceが押されたとき
      if(mode == -3){
        mode = -1;
        break;
      }
      if(mode == -1){
        mode = 1;
      }else if(mode >= 1 && sc.ftr.hp > 0){
        sc.ftr.sf = true;
      }
      break;
      case KeyEvent.VK_V://Vが押されたとき
      sc.ftr.vf = true;
      break;
      case KeyEvent.VK_P://Pが押されたとき
      if(mode != -2){
        pmode = mode;
        mode = -2;
      }else{
        mode = pmode;
      }
      break;
    }
  }


  public void keyReleased(KeyEvent ke){// キーが離されたとき
    int cd = ke.getKeyCode();
    //System.out.println("Released");
    switch(cd){
      case KeyEvent.VK_LEFT://左矢印が離されたとき
      sc.ftr.lf = false;
      break;
      case KeyEvent.VK_RIGHT://右矢印が離されたとき
      sc.ftr.rf = false;
      break;
      case KeyEvent.VK_UP://上矢印が離されたとき
      sc.ftr.uf = false;
      break;
      case KeyEvent.VK_DOWN://下矢印が離されたとき
      sc.ftr.df = false;
      break;
      case KeyEvent.VK_Z://Zが離されたとき
      sc.ftr.zf = false;
      break;
      case KeyEvent.VK_C://Cが離されたとき
      sc.ftr.cf = false;
      break;

    }
  }
  private void drawNumber(int wide, int height, int n, Graphics g){// 数字を描画
    int n1 = n % 10;
    int n2 = (n - n1) / 10;
    if(n2 == 0){// 数字が0～9
      g.drawImage(numbersImg, wide , height, wide+24, height+48,  n1*24, 0, n1*24+24, 48,  null);
    }else{// 数字が10～99
      g.drawImage(numbersImg, wide, height, wide+24, height+48,  n2*24, 0, n2*24+24, 48, null);
      g.drawImage(numbersImg, wide+24, height, wide+24*2, height+48, n1*24, 0, n1*24+24, 48, null);
    }
  }
}
