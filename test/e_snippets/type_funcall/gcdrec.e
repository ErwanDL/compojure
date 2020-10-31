int gcd(int a, int b){
 if(b == 0){
   return a;
 } else {
   return gcd(b, a % b);
 }
}
int main(int a, int b){
  return gcd(a,b);
}
