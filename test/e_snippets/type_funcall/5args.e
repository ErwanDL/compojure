int f(int x, int y, int z, int t, int u){
  return x + y + z + t + u;
}
int main(int x, int y){
  return f(x,x,y,y,x*2);
}
