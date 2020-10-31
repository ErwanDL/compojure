struct point {
  int x;
  int y;
};

int scalar_product(struct point p1, struct point p2){
  return p1.x * p2.x + p1.y * p2.y;
}

int orthogonal(struct point p1, struct point p2){
  return p1.x * p2.y - p1.y * p2.x;
}


int main(){
  struct point p1;
  struct point p2;
  p1.x = 3;
  p1.y = 3;
  p2.x = -5;
  p2.y = 4;
  
  print(scalar_product(p1,p2));
  return (orthogonal(p1,p2));
}

