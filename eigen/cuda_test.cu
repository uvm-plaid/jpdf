#include <iostream>
#include <math.h>
#include <Eigen/Dense>

__global__
void mult_vect(int n, float *x, float *y)
{
  int index = blockIdx.x * blockDim.x + threadIdx.x;
  int stride = blockDim.x * gridDim.x;

  for (int i = index; i < n; i+=stride)
      y[i] = x[i] * y[i];
}

int main(void)
{
  int n = 1<<10; 

  float *x, *y;

  cudaMallocManaged(&x, n*sizeof(float));
  cudaMallocManaged(&y, N*sizeof(float));

  for (int i = 0; i < n; i++) {
    x[i] = 1.0f;
    y[i] = 2.0f;
  }

  int blockSize = 256;
  int numBlocks = (n + blockSize - 1) / blockSize;

  mult_vect<<<numBlocks, blockSize>>>(n, x, y);

  cudaDeviceSynchronize();
  
  // float maxError = 0.0f;
  // for (int i = 0; i < n; i++)
  //   maxError = fmax(maxError, fabs(y[i]-3.0f));
  // std::cout << "error rate: " << maxError << std::endl;

  cudaFree(x);
  cudaFree(y);

  return 0;
}