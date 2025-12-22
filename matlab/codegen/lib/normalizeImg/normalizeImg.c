/*
 * File: normalizeImg.c
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:29:50
 */

/* Include Files */
#include "normalizeImg.h"
#include "blockedSummation.h"
#include "normalizeImg_data.h"
#include "normalizeImg_emxutil.h"
#include "normalizeImg_initialize.h"
#include "normalizeImg_types.h"
#include "rt_nonfinite.h"
#include "rt_nonfinite.h"
#include <emmintrin.h>
#include <math.h>

/* Function Definitions */
/*
 * Arguments    : const emxArray_real_T *b_I
 *                double M0
 *                double VAR0
 *                emxArray_real_T *I_norm
 * Return Type  : void
 */
void normalizeImg(const emxArray_real_T *b_I, double M0, double VAR0,
                  emxArray_real_T *I_norm)
{
  __m128d r;
  emxArray_real_T c_I;
  emxArray_real_T *x;
  const double *I_data;
  double M;
  double VAR;
  double t;
  double xbar;
  double *I_norm_data;
  double *x_data;
  int d_I;
  int e_I;
  int i;
  int k;
  int nx;
  int vectorUB;
  if (!isInitialized_normalizeImg) {
    normalizeImg_initialize();
  }
  I_data = b_I->data;
  nx = b_I->size[0] * b_I->size[1];
  c_I = *b_I;
  d_I = nx;
  c_I.size = &d_I;
  c_I.numDimensions = 1;
  M = blockedSummation(&c_I, nx) / (double)nx;
  if (nx == 0) {
    VAR = rtNaN;
  } else if (nx == 1) {
    if ((!rtIsInf(I_data[0])) && (!rtIsNaN(I_data[0]))) {
      VAR = 0.0;
    } else {
      VAR = rtNaN;
    }
  } else {
    c_I = *b_I;
    e_I = nx;
    c_I.size = &e_I;
    c_I.numDimensions = 1;
    xbar = blockedSummation(&c_I, nx) / (double)nx;
    VAR = 0.0;
    for (k = 0; k < nx; k++) {
      t = I_data[k] - xbar;
      VAR += t * t;
    }
    VAR /= (double)(nx - 1);
  }
  xbar = sqrt(VAR0 / VAR);
  emxInit_real_T(&x, 2);
  i = x->size[0] * x->size[1];
  x->size[0] = b_I->size[0];
  x->size[1] = b_I->size[1];
  emxEnsureCapacity_real_T(x, i);
  x_data = x->data;
  k = (nx / 2) << 1;
  vectorUB = k - 2;
  for (i = 0; i <= vectorUB; i += 2) {
    _mm_storeu_pd(&x_data[i],
                  _mm_sub_pd(_mm_loadu_pd(&I_data[i]), _mm_set1_pd(M)));
  }
  for (i = k; i < nx; i++) {
    x_data[i] = I_data[i] - M;
  }
  nx = x->size[0] * x->size[1];
  i = I_norm->size[0] * I_norm->size[1];
  I_norm->size[0] = x->size[0];
  I_norm->size[1] = x->size[1];
  emxEnsureCapacity_real_T(I_norm, i);
  I_norm_data = I_norm->data;
  for (k = 0; k < nx; k++) {
    I_norm_data[k] = fabs(x_data[k]);
  }
  emxFree_real_T(&x);
  nx = I_norm->size[0] * I_norm->size[1];
  k = (nx / 2) << 1;
  vectorUB = k - 2;
  for (i = 0; i <= vectorUB; i += 2) {
    r = _mm_loadu_pd(&I_norm_data[i]);
    _mm_storeu_pd(
        &I_norm_data[i],
        _mm_add_pd(_mm_set1_pd(M0), _mm_mul_pd(_mm_set1_pd(xbar), r)));
  }
  for (i = k; i < nx; i++) {
    I_norm_data[i] = M0 + xbar * I_norm_data[i];
  }
}

/*
 * File trailer for normalizeImg.c
 *
 * [EOF]
 */
