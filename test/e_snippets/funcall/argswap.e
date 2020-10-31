g(a,b){
  print(a);
  print(b);
  return (b - a);
}
f(a,b){
  print(a);
  print(b);
  return g(b, a);
}
main(x, y){
  return f(x, y);
}
