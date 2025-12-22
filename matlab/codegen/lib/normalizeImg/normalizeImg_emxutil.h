/*
 * File: normalizeImg_emxutil.h
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:29:50
 */

#ifndef NORMALIZEIMG_EMXUTIL_H
#define NORMALIZEIMG_EMXUTIL_H

/* Include Files */
#include "normalizeImg_types.h"
#include "rtwtypes.h"
#include <stddef.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Function Declarations */
extern void emxEnsureCapacity_real_T(emxArray_real_T *emxArray, int oldNumel);

extern void emxFree_real_T(emxArray_real_T **pEmxArray);

extern void emxInit_real_T(emxArray_real_T **pEmxArray, int numDimensions);

#ifdef __cplusplus
}
#endif

#endif
/*
 * File trailer for normalizeImg_emxutil.h
 *
 * [EOF]
 */
