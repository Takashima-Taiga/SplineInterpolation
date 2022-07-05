package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * ��Ԃ�\���D
 */
public final class Interval {
  /**
   * ���[begin,end]�𐶐�����D
   *
   * @param _begin ��Ԃ̎n�_
   * @param _end ��Ԃ̏I�_
   * @return ���[begin,end]
   * @throws IllegalArgumentException begin > end�ł������ꍇ
   */
  public static Interval create(Double _begin, Double _end) {
    if (_begin > _end) {
      throw new IllegalArgumentException("_begin and _end must be _begin < _end.");
    }

    return new Interval(_begin, _end);
  }

  /**
   * �w�肵���l����ԂɊ܂܂�Ă��邩�𒲂ׂ�D ��ԂɊ܂܂�Ă���ꍇ��true�C����ȊO�̏ꍇ��false��Ԃ��D
   *
   * @param _t ��ԓ��ł��邩�𒲂ׂ�l
   * @return ��ԂɊ܂܂�Ă��邩
   */
  public boolean contains(Double _t) {
    return m_begin <= _t && _t <= m_end;
  }

  /**
   * ��Ԃ̎n�_���擾����D
   *
   * @return ��Ԃ̎n�_
   */
  public Double getBegin() {
    return m_begin;
  }

  /**
   * ��Ԃ̏I�_���擾����D
   *
   * @return ��Ԃ̏I�_
   */
  public Double getEnd() {
    return m_end;
  }

  /**
   * ��Ԃ�_num - 1��������悤��_num�̃p�����[�^�̃��X�g�i�n�_�ƏI�_���܂ށj�𐶐�����D
   *
   * @param _num �T���v����
   * @return �T���v�����O�����l
   * @throws IllegalArgumentException �T���v�������s���Ȓl�ł������ꍇ
   */
  public List<Double> sample(Integer _num) {
    List<Double> samples = new ArrayList<>();

    // _num=1�̂Ƃ��A0�����ƂȂ�0���Z���������邪�A
    // m_begin==m_end�̏ꍇ�̂݁A�ǂ��炩��samples�ɓ���ĕԂ�l�Ƃ���
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
   * �R���X�g���N�^
   *
   * @param _begin ��Ԃ̎n�_
   * @param _end ��Ԃ̏I�_
   */
  private Interval(Double _begin, Double _end) {
    m_begin = _begin;
    m_end = _end;
  }

  /** ��Ԃ̎n�_ */
  private final Double m_begin;
  /** ��Ԃ̏I�_ */
  private final Double m_end;
}
