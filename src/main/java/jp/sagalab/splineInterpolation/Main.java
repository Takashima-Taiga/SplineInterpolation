package jp.sagalab.splineInterpolation;

import jp.sagalab.Interval;
import jp.sagalab.Point;

import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.awt.Canvas;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import jp.sagalab.SplineCurve;
import org.apache.commons.lang3.time.StopWatch;

public class Main extends JFrame {
  public Main() {
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
    addWindowListener(new WindowClosing());
    setState(JFrame.ICONIFIED);
    //setIconImage(new ImageIcon("icon.jpg").getImage());
    m_canvas.setSize(800, 600);
    m_canvas.setBackground(Color.WHITE);
    setTitle("splineCurve");
    add(m_canvas);
    pack();
    setVisible( true );

    m_canvas.addMouseMotionListener(
      new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
          Double time = stopwatch.getTime() / 1000.0;
          Point current = Point.create((double)e.getX(), (double)e.getY(), time);

          if (isDrawing && m_last != null) {
            drawLine(m_last, current, Color.BLACK);
          }

          m_last = current;
          m_passPoints.add(current);
        }
      }
    );

    m_canvas.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          isDrawing = true;
          stopwatch.reset();
          stopwatch.start();

          if (m_passPoints.isEmpty()) {
            Double time = stopwatch.getTime() / 1000.0;
            m_passPoints.add(Point.create((double)e.getX(), (double)e.getY(), time));
          }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          isDrawing = false;
          m_last = null;
          stopwatch.stop();

          SplineCurveInterpolation sci = SplineCurveInterpolation.create(m_passPoints, 3);
          List<Point> controlPoints = new ArrayList<>(sci.getControlPoints());

          SplineCurve sc = SplineCurve.create(controlPoints, sci.getKnots());

          Interval domain = sc.getDomain();
          Point tmp = sc.evaluate(domain.getBegin());
          double interval = 0.01;
          double loopCount = (domain.getEnd() - domain.getBegin()) / interval;

          for (int i = 1; i <= loopCount; ++i) {
            double t = domain.getBegin() + interval * i;

            Point p = sc.evaluate(t);

            drawLine(tmp, p, Color.RED);

            tmp = p;
          }

          m_passPoints.clear();
        }
      }
    );
  }

  ActionListener taskPerformer = new ActionListener() {
    public void actionPerformed(ActionEvent evt) {
    }
  };

  /**
   * 点を描画する
   * @param _x x座標
   * @param _y y座標
   */
  public void drawPoint(double _x, double _y) {
    Graphics2D g = (Graphics2D)m_canvas.getGraphics();
    double radius = 8;
    Ellipse2D.Double oval = new Ellipse2D.Double(_x, _y, radius, radius);
    g.draw(oval);
  }

  /**
   * 線を描画する
   * @param _p1 始点
   * @param _p2 終点
   */
  public void drawLine(Point _p1, Point _p2, Color _color) {
    Graphics2D g = (Graphics2D)m_canvas.getGraphics();
    g.setColor(_color);
    Point2D.Double p1 = new Point2D.Double(_p1.getX(), _p1.getY());
    Point2D.Double p2 = new Point2D.Double(_p2.getX(), _p2.getY());
    Line2D.Double line = new Line2D.Double(p1, p2);
    g.draw(line);
  }

  /**
   * @param _args the command line arguments
   */
  public static void main(String[] _args){ new Main(); }

  /** キャンバスを表す変数 */
  private Canvas m_canvas = new Canvas();

  private List<Point> m_passPoints = new ArrayList<>();

  private Point m_last;
  private boolean isDrawing = false;

  private StopWatch stopwatch = new StopWatch();

  private static final double TIME_INTERVAL = 0.0001;

  /**
   * ウィンドウを閉じる時の確認ダイアログを表すクラス
   */
  class WindowClosing extends WindowAdapter {
    public void windowClosing(WindowEvent _e){
      int ans = JOptionPane.showConfirmDialog(Main.this, "Are you sure you want to finish?");
      if(ans == JOptionPane.YES_OPTION){
        System.exit(0);
      }
    }
  }
}

