main(ndisque){
  return hanoi(ndisque);
}
hanoi(n)
{
  if(n > 0)
    {
      x = hanoi(n-1);
      return 1 + 2 * x;
    }
  return 0;
}
