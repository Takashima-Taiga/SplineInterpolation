package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * 任意の次数のスプライン曲線を表す．
 * このクラスでは次数を指定せず，制御点とノット列からスプライン曲線を生成する． 次数は以下の式から求めることができる．
 *
 * 次数 = ノットの数 - 制御点の数 - 1
 */
public class SplineCurve {

  /**
   * 制御点列とノット列を指定してスプライン曲線のオブジェクトを生成する．
   *
   * @param _controlPoints 制御点列
   * @param _knots ノット列
   * @return スプライン曲線のオブジェクト
   * @throws IllegalArgumentException 制御点数が次数以下であった場合
   */
  public static SplineCurve create(List<Point> _controlPoints, List<Double> _knots) {
    final int cpsSize = _controlPoints.size();
    final int knotsSize = _knots.size();

    if (!(cpsSize <= knotsSize && knotsSize <= (2 * cpsSize - 2))) {
      throw new IllegalArgumentException("_controlPoints and _knots size must be cpsSize <= knotsSize <= 2 * cpsSize - 2");
    }

    return new SplineCurve(_controlPoints, _knots);
  }

  /**
   * 制御点列のコピーを取得する．
   *
   * @return 制御点列のコピー
   */
  public List<Point> getControlPoints() {
    return new ArrayList<>(m_controlPoints);
  }

  /**
   * ノット列のコピーを取得する．
   *
   * @return ノット列のコピー
   */
  public List<Double> getKnots() {
    return new ArrayList<>(m_knots);
  }

  /**
   * 次数を取得する．
   *
   * @return 次数
   */
  public Integer getDegree() {
    return m_knots.size() - m_controlPoints.size() + 1;
  }

  /**
   * 定義域を取得する．
   *
   * @return 定義域
   */
  public Interval getDomain() {
    final Integer k = getDegree();
    final Integer l = numberOfSections();
    final Integer startIndex = k - 1;
    final Integer endIndex = k + l - 1;

    return Interval.create(m_knots.get(startIndex), m_knots.get(endIndex));
  }

  public Integer numberOfSections() {
    Integer cpsSize = m_controlPoints.size();
    Integer knotsSize = m_knots.size();

    return 2 * cpsSize - knotsSize - 1;
  }

  /**
   * パラメーターtに対応する評価点を求める． パラメーターtは定義域内の値である必要がある．
   *
   * @param _t 定義域内のパラメーターt
   * @return パラメーターtに対応する評価点
   * @throws IllegalArgumentException パラメーターtが定義域外であった場合
   */
  public Point evaluate(Double _t) {
    if (!getDomain().contains(_t)) {
      throw new IllegalArgumentException("_t out of domain.");
    }

    double x = 0.0;
    double y = 0.0;
    final Integer degree = getDegree();

    for (int i = 0; i < m_controlPoints.size(); ++i) {
      final Double basis = basisFunction(i, degree, _t);

      x += m_controlPoints.get(i).getX() * basis;
      y += m_controlPoints.get(i).getY() * basis;
    }

    return Point.create(x, y);
  }

  /**
   * 基底関数（B-Spline）
   *
   * @param _i 制御点列内の対象の制御点のインデックス
   * @param _k 次数
   * @param _t 定義域内のパラメーターt
   * @return 基底関数の値
   */
  private Double basisFunction(Integer _i, Integer _k, Double _t) {
    int cpsSize = m_controlPoints.size();

    { // 特別な場合の処理（左端・右端の基底関数だったり、節点列の終端がn重節点だったり）
      int knotsSize = m_knots.size();
      int n = knotsSize - cpsSize + 1;

      // 左端では u(-1) は考慮しない（p.48 図3.14、p.50 図3.16、図3.17）
      if (_i == 0) {
        double coeff = (m_knots.get(_i + _k) - _t) / (m_knots.get(_i + _k) - m_knots.get(_i));
        return coeff * basisFunction(_i + 1, _k - 1, _t);
      }

      // 右端では u(knotsSize) は考慮しない（p.48 図3.14、p.50 図3.16、図3.17）
      if (_i + _k == knotsSize) {
        double coeff = (_t - m_knots.get(_i - 1)) / (m_knots.get(_i + _k - 1) - m_knots.get(_i - 1));
        return coeff * basisFunction(_i, _k - 1, _t);
      }

      if (_k == 0) {
        // 節点列の終端がn重節点になっているか
        boolean isN_Overlapped = m_knots.get(m_knots.size() - 1).equals(m_knots.get(m_knots.size() - n));

        if (_i == cpsSize - 1 && m_knots.get(knotsSize - 1).equals(_t) && isN_Overlapped) {
          return 1.0;
        }

        return (m_knots.get(_i - 1) <= _t && _t < m_knots.get(_i)) ? 1.0 : 0.0;
      }
    }

    // 通常処理
    // 分母を先に計算して0になったら（0除算が発生しそうなら）その項の係数は0とする
    double denom1 = m_knots.get(_i + _k - 1) - m_knots.get(_i - 1);
    double denom2 = m_knots.get(_i + _k) - m_knots.get(_i);
    double coeff1 = (denom1 != 0.0) ? (_t - m_knots.get(_i - 1)) / denom1 : 0.0;
    double coeff2 = (denom2 != 0.0) ? (m_knots.get(_i + _k) - _t) / denom2 : 0.0;

    return coeff1 * basisFunction(_i, _k - 1, _t)
            + coeff2 * basisFunction(_i + 1, _k - 1, _t);
  }

  /**
   * コンストラクタ
   *
   * @param _controlPoints 制御点列
   * @param _knots ノット列
   */
  private SplineCurve(List<Point> _controlPoints, List<Double> _knots) {
    m_controlPoints = _controlPoints;
    m_knots = _knots;
  }

  /** 制御点列 */
  private final List<Point> m_controlPoints;
  /** ノット列 */
  private final List<Double> m_knots;
}
