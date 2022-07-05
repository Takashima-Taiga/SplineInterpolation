package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * 区間を表す．
 */
public final class Interval {
  /**
   * 閉区間[begin,end]を生成する．
   *
   * @param _begin 閉区間の始点
   * @param _end 閉区間の終点
   * @return 閉区間[begin,end]
   * @throws IllegalArgumentException begin > endであった場合
   */
  public static Interval create(Double _begin, Double _end) {
    if (_begin > _end) {
      throw new IllegalArgumentException("_begin and _end must be _begin < _end.");
    }

    return new Interval(_begin, _end);
  }

  /**
   * 指定した値が区間に含まれているかを調べる． 区間に含まれている場合はtrue，それ以外の場合はfalseを返す．
   *
   * @param _t 区間内であるかを調べる値
   * @return 区間に含まれているか
   */
  public boolean contains(Double _t) {
    return m_begin <= _t && _t <= m_end;
  }

  /**
   * 区間の始点を取得する．
   *
   * @return 区間の始点
   */
  public Double getBegin() {
    return m_begin;
  }

  /**
   * 区間の終点を取得する．
   *
   * @return 区間の終点
   */
  public Double getEnd() {
    return m_end;
  }

  /**
   * 区間を_num - 1等分するような_num個のパラメータのリスト（始点と終点を含む）を生成する．
   *
   * @param _num サンプル数
   * @return サンプリングした値
   * @throws IllegalArgumentException サンプル数が不正な値であった場合
   */
  public List<Double> sample(Integer _num) {
    List<Double> samples = new ArrayList<>();

    // _num=1のとき、0等分となり0除算が発生するが、
    // m_begin==m_endの場合のみ、どちらかをsamplesに入れて返り値とする
    if (_num <= 1) {
      if (m_begin.equals(m_end)) {
        if (_num != 1) {
          throw new IllegalArgumentException("if begin == end, _num must be 1.");
        }

        samples.add(m_begin);

        return samples;
      }

      throw new IllegalArgumentException("_num must be more than 1.");
    }

    final Double increase = (m_end - m_begin) / (_num.doubleValue() - 1.0);

    for (int i = 0; i < _num; ++i) {
      samples.add(m_begin + i * increase);
    }

    return samples;
  }

  /**
   * コンストラクタ
   *
   * @param _begin 閉区間の始点
   * @param _end 閉区間の終点
   */
  private Interval(Double _begin, Double _end) {
    m_begin = _begin;
    m_end = _end;
  }

  /** 閉区間の始点 */
  private final Double m_begin;
  /** 閉区間の終点 */
  private final Double m_end;
}
