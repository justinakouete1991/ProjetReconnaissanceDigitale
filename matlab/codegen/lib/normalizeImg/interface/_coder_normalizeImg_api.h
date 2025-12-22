/*
 * File: _coder_normalizeImg_api.h
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:29:50
 */

#ifndef _CODER_NORMALIZEIMG_API_H
#define _CODER_NORMALIZEIMG_API_H

/* Include Files */
#include "emlrt.h"
#include "mex.h"
#include "tmwtypes.h"
#include <string.h>

/* Type Definitions */
#ifndef struct_emxArray_real_T
#define struct_emxArray_real_T
struct emxArray_real_T {
  real_T *data;
  int32_T *size;
  int32_T allocatedSize;
  int32_T numDimensions;
  boolean_T canFreeData;
};
#endif /* struct_emxArray_real_T */
#ifndef typedef_emxArray_real_T
#define typedef_emxArray_real_T
typedef struct emxArray_real_T emxArray_real_T;
#endif /* typedef_emxArray_real_T */

/* Variable Declarations */
extern emlrtCTX emlrtRootTLSGlobal;
extern emlrtContext emlrtContextGlobal;

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
void normalizeImg(emxArray_real_T *b_I, real_T M0, real_T VAR0,
                  emxArray_real_T *I_norm);

void normalizeImg_api(const mxArray *const prhs[3], const mxArray **plhs);

void normalizeImg_atexit(void);

void normalizeImg_initialize(void);

void normalizeImg_terminate(void);

void normalizeImg_xil_shutdown(void);

void normalizeImg_xil_terminate(void);

#ifdef __cplusplus
}
#endif

#endif
/*
 * File trailer for _coder_normalizeImg_api.h
 *
 * [EOF]
 */
