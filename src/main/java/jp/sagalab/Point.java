package jp.sagalab;

/**
 * ���ʏ�̓_��\���D ���ʏ�̓_��x���W��y���W��ێ�����D
 */
public final class Point {
  /**
   * �w�肵�����W�ɂ���_�𐶐�����D
   *
   * @param _x x���W
   * @param _y y���W
   * @return �_
   */
  public static Point create(Double _x, Double _y) {
    return new Point(_x, _y);
  }

  public static Point create(Double _x, Double _y, Double _t) {
    return new Point(_x, _y, _t);
  }

  /**
   * �_��x���W���擾����D
   *
   * @return x���W
   */
  public Double getX() {
    return m_x;
  }

  /**
   * �_��y���W���擾����D
   *
   * @return y���W
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

  /** x���W */
  private final Double m_x;
  /** y���W */
  private final Double m_y;
  /** ���� */
  private final Double m_t;
}
