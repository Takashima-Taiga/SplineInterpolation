package jp.sagalab;

/**
 * 平面上の点を表す． 平面上の点のx座標とy座標を保持する．
 */
public final class Point {
  /**
   * 指定した座標にある点を生成する．
   *
   * @param _x x座標
   * @param _y y座標
   * @return 点
   */
  public static Point create(Double _x, Double _y) {
    return new Point(_x, _y);
  }

  public static Point create(Double _x, Double _y, Double _t) {
    return new Point(_x, _y, _t);
  }

  /**
   * 点のx座標を取得する．
   *
   * @return x座標
   */
  public Double getX() {
    return m_x;
  }

  /**
   * 点のy座標を取得する．
   *
   * @return y座標
   */
  public Double getY() {
    return m_y;
  }

  public Double getTime() {
    return m_t;
  }

  Point(Double _x, Double _y) {
    m_x = _x;
    m_y = _y;
    m_t = 0.0;
  }

  Point(Double _x, Double _y, Double _t) {
    m_x = _x;
    m_y = _y;
    m_t = _t;
  }

  /** x座標 */
  private final Double m_x;
  /** y座標 */
  private final Double m_y;
  /** 時刻 */
  private final Double m_t;
}
