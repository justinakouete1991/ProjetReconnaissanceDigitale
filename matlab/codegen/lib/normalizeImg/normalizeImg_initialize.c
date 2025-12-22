/*
 * File: normalizeImg_initialize.c
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:29:50
 */

/* Include Files */
#include "normalizeImg_initialize.h"
#include "normalizeImg_data.h"
#include "rt_nonfinite.h"

/* Function Definitions */
/*
 * Arguments    : void
 * Return Type  : void
 */
void normalizeImg_initialize(void)
{
  rt_InitInfAndNaN();
  isInitialized_normalizeImg = true;
}

/*
 * File trailer for normalizeImg_initialize.c
 *
 * [EOF]
 */
