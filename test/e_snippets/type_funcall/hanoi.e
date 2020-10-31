int hanoi(int n)
{
  if(n > 0)
    {
      int x = hanoi(n-1);
      return 1 + 2 * x;
    }
  return 0;
}
int main(int ndisque){
  return hanoi(ndisque);
}
