/*
 * File: main.c
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:29:50
 */

/*************************************************************************/
/* This automatically generated example C main file shows how to call    */
/* entry-point functions that MATLAB Coder generated. You must customize */
/* this file for your application. Do not modify this file directly.     */
/* Instead, make a copy of this file, modify it, and integrate it into   */
/* your development environment.                                         */
/*                                                                       */
/* This file initializes entry-point function arguments to a default     */
/* size and value before calling the entry-point functions. It does      */
/* not store or use any values returned from the entry-point functions.  */
/* If necessary, it does pre-allocate memory for returned values.        */
/* You can use this file as a starting point for a main function that    */
/* you can deploy in your application.                                   */
/*                                                                       */
/* After you copy the file, and before you deploy it, you must make the  */
/* following changes:                                                    */
/* * For variable-size function arguments, change the example sizes to   */
/* the sizes that your application requires.                             */
/* * Change the example values of function arguments to the values that  */
/* your application requires.                                            */
/* * If the entry-point functions return values, store these values or   */
/* otherwise use them as required by your application.                   */
/*                                                                       */
/*************************************************************************/

/* Include Files */
#include "main.h"
#include "normalizeImg.h"
#include "normalizeImg_emxAPI.h"
#include "normalizeImg_terminate.h"
#include "normalizeImg_types.h"
#include "rt_nonfinite.h"

/* Function Declarations */
static double argInit_real_T(void);

static emxArray_real_T *c_argInit_UnboundedxUnbounded_r(void);

/* Function Definitions */
/*
 * Arguments    : void
 * Return Type  : double
 */
static double argInit_real_T(void)
{
  return 0.0;
}

/*
 * Arguments    : void
 * Return Type  : emxArray_real_T *
 */
static emxArray_real_T *c_argInit_UnboundedxUnbounded_r(void)
{
  emxArray_real_T *result;
  double *result_data;
  int idx0;
  int idx1;
  /* Set the size of the array.
Change this size to the value that the application requires. */
  result = emxCreate_real_T(2, 2);
  result_data = result->data;
  /* Loop over the array to initialize each element. */
  for (idx0 = 0; idx0 < result->size[0U]; idx0++) {
    for (idx1 = 0; idx1 < result->size[1U]; idx1++) {
      /* Set the value of the array element.
Change this value to the value that the application requires. */
      result_data[idx0 + result->size[0] * idx1] = argInit_real_T();
    }
  }
  return result;
}

/*
 * Arguments    : int argc
 *                char **argv
 * Return Type  : int
 */
int main(int argc, char **argv)
{
  (void)argc;
  (void)argv;
  /* The initialize function is being called automatically from your entry-point
   * function. So, a call to initialize is not included here. */
  /* Invoke the entry-point functions.
You can call entry-point functions multiple times. */
  main_normalizeImg();
  /* Terminate the application.
You do not need to do this more than one time. */
  normalizeImg_terminate();
  return 0;
}

/*
 * Arguments    : void
 * Return Type  : void
 */
void main_normalizeImg(void)
{
  emxArray_real_T *I_norm;
  emxArray_real_T *b_I;
  double M0_tmp;
  /* Initialize function 'normalizeImg' input arguments. */
  /* Initialize function input argument 'I'. */
  b_I = c_argInit_UnboundedxUnbounded_r();
  M0_tmp = argInit_real_T();
  /* Call the entry-point 'normalizeImg'. */
  emxInitArray_real_T(&I_norm, 2);
  normalizeImg(b_I, M0_tmp, M0_tmp, I_norm);
  emxDestroyArray_real_T(b_I);
  emxDestroyArray_real_T(I_norm);
}

/*
 * File trailer for main.c
 *
 * [EOF]
 */
