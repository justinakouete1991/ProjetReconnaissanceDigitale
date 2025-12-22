/*
 * File: _coder_test_connexion_2_mex.c
 *
 * MATLAB Coder version            : 24.1
 * C/C++ source code generated on  : 20-Dec-2025 22:12:02
 */

/* Include Files */
#include "_coder_test_connexion_2_mex.h"
#include "_coder_test_connexion_2_api.h"

/* Function Definitions */
/*
 * Arguments    : int32_T nlhs
 *                mxArray *plhs[]
 *                int32_T nrhs
 *                const mxArray *prhs[]
 * Return Type  : void
 */
void mexFunction(int32_T nlhs, mxArray *plhs[], int32_T nrhs,
                 const mxArray *prhs[])
{
  (void)plhs;
  (void)prhs;
  mexAtExit(&test_connexion_2_atexit);
  /* Module initialization. */
  test_connexion_2_initialize();
  /* Dispatch the entry-point. */
  unsafe_test_connexion_2_mexFunction(nlhs, nrhs);
  /* Module termination. */
  test_connexion_2_terminate();
}

/*
 * Arguments    : void
 * Return Type  : emlrtCTX
 */
emlrtCTX mexFunctionCreateRootTLS(void)
{
  emlrtCreateRootTLSR2022a(&emlrtRootTLSGlobal, &emlrtContextGlobal, NULL, 1,
                           NULL, "windows-1252", true);
  return emlrtRootTLSGlobal;
}

/*
 * Arguments    : int32_T nlhs
 *                int32_T nrhs
 * Return Type  : void
 */
void unsafe_test_connexion_2_mexFunction(int32_T nlhs, int32_T nrhs)
{
  emlrtStack st = {
      NULL, /* site */
      NULL, /* tls */
      NULL  /* prev */
  };
  st.tls = emlrtRootTLSGlobal;
  /* Check for proper number of arguments. */
  if (nrhs != 0) {
    emlrtErrMsgIdAndTxt(&st, "EMLRT:runTime:WrongNumberOfInputs", 5, 12, 0, 4,
                        16, "test_connexion_2");
  }
  if (nlhs > 0) {
    emlrtErrMsgIdAndTxt(&st, "EMLRT:runTime:TooManyOutputArguments", 3, 4, 16,
                        "test_connexion_2");
  }
  /* Call the function. */
  test_connexion_2_api();
}

/*
 * File trailer for _coder_test_connexion_2_mex.c
 *
 * [EOF]
 */
