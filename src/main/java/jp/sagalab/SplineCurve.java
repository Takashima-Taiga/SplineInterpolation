package jp.sagalab;

import java.util.ArrayList;
import java.util.List;

/**
 * �C�ӂ̎����̃X�v���C���Ȑ���\���D
 * ���̃N���X�ł͎������w�肹���C����_�ƃm�b�g�񂩂�X�v���C���Ȑ��𐶐�����D �����͈ȉ��̎����狁�߂邱�Ƃ��ł���D
 *
 * ���� = �m�b�g�̐� - ����_�̐� - 1
 */
public class SplineCurve {

  /**
   * ����_��ƃm�b�g����w�肵�ăX�v���C���Ȑ��̃I�u�W�F�N�g�𐶐�����D
   *
   * @param _controlPoints ����_��
   * @param _knots �m�b�g��
   * @return �X�v���C���Ȑ��̃I�u�W�F�N�g
   * @throws IllegalArgumentException ����_���������ȉ��ł������ꍇ
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
   * ����_��̃R�s�[���擾����D
   *
   * @return ����_��̃R�s�[
   */
  public List<Point> getControlPoints() {
    return new ArrayList<>(m_controlPoints);
  }

  /**
   * �m�b�g��̃R�s�[���擾����D
   *
   * @return �m�b�g��̃R�s�[
   */
  public List<Double> getKnots() {
    return new ArrayList<>(m_knots);
  }

  /**
   * �������擾����D
   *
   * @return ����
   */
  public Integer getDegree() {
    return m_knots.size() - m_controlPoints.size() + 1;
  }

  /**
   * ��`����擾����D
   *
   * @return ��`��
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
   * �p�����[�^�[t�ɑΉ�����]���_�����߂�D �p�����[�^�[t�͒�`����̒l�ł���K�v������D
   *
   * @param _t ��`����̃p�����[�^�[t
   * @return �p�����[�^�[t�ɑΉ�����]���_
   * @throws IllegalArgumentException �p�����[�^�[t����`��O�ł������ꍇ
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
   * ���֐��iB-Spline�j
   *
   * @param _i ����_����̑Ώۂ̐���_�̃C���f�b�N�X
   * @param _k ����
   * @param _t ��`����̃p�����[�^�[t
   * @return ���֐��̒l
   */
  private Double basisFunction(Integer _i, Integer _k, Double _t) {
    int cpsSize = m_controlPoints.size();

    { // ���ʂȏꍇ�̏����i���[�E�E�[�̊��֐���������A�ߓ_��̏I�[��n�d�ߓ_��������j
      int knotsSize = m_knots.size();
      int n = knotsSize - cpsSize + 1;

      // ���[�ł� u(-1) �͍l�����Ȃ��ip.48 �}3.14�Ap.50 �}3.16�A�}3.17�j
      if (_i == 0) {
        double coeff = (m_knots.get(_i + _k) - _t) / (m_knots.get(_i + _k) - m_knots.get(_i));
        return coeff * basisFunction(_i + 1, _k - 1, _t);
      }

      // �E�[�ł� u(knotsSize) �͍l�����Ȃ��ip.48 �}3.14�Ap.50 �}3.16�A�}3.17�j
      if (_i + _k == knotsSize) {
        double coeff = (_t - m_knots.get(_i - 1)) / (m_knots.get(_i + _k - 1) - m_knots.get(_i - 1));
        return coeff * basisFunction(_i, _k - 1, _t);
      }

      if (_k == 0) {
        // �ߓ_��̏I�[��n�d�ߓ_�ɂȂ��Ă��邩
        boolean isN_Overlapped = m_knots.get(m_knots.size() - 1).equals(m_knots.get(m_knots.size() - n));

        if (_i == cpsSize - 1 && m_knots.get(knotsSize - 1).equals(_t) && isN_Overlapped) {
          return 1.0;
        }

        return (m_knots.get(_i - 1) <= _t && _t < m_knots.get(_i)) ? 1.0 : 0.0;
      }
    }

    // �ʏ폈��
    // ������Ɍv�Z����0�ɂȂ�����i0���Z�������������Ȃ�j���̍��̌W����0�Ƃ���
    double denom1 = m_knots.get(_i + _k - 1) - m_knots.get(_i - 1);
    double denom2 = m_knots.get(_i + _k) - m_knots.get(_i);
    double coeff1 = (denom1 != 0.0) ? (_t - m_knots.get(_i - 1)) / denom1 : 0.0;
    double coeff2 = (denom2 != 0.0) ? (m_knots.get(_i + _k) - _t) / denom2 : 0.0;

    return coeff1 * basisFunction(_i, _k - 1, _t)
            + coeff2 * basisFunction(_i + 1, _k - 1, _t);
  }

  /**
   * �R���X�g���N�^
   *
   * @param _controlPoints ����_��
   * @param _knots �m�b�g��
   */
  private SplineCurve(List<Point> _controlPoints, List<Double> _knots) {
    m_controlPoints = _controlPoints;
    m_knots = _knots;
  }

  /** ����_�� */
  private final List<Point> m_controlPoints;
  /** �m�b�g�� */
  private final List<Double> m_knots;
}
