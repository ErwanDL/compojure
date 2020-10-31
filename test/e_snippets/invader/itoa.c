#include<stdio.h>

char * itoa(signed int i, char* buf){
  char *s = buf + 10;
  *s = '\0';
  char neg = 0;
  if (i < 0) {
	  neg = 1;
	  i = - i;
  }
  if (i == 0){
	  *--s = '0';
	  return s;
  }
  while(i > 0){
	
	*--s = (i % 10) + '0';
	i = i / 10;
  }
  if (neg){
	  *--s = '-';
  }
  return s;
}

/* int main(){ */
/* char buf[20] = "abcdefghijklmnopqrst"; */
/*  printf("itoa(-23467) = %s\n", itoa(-23467,buf)); */
/*  printf("itoa(1238) = %s\n", itoa(1238,buf)); */
/*  printf("itoa(0) = %s\n", itoa(0,buf)); */
/*  printf("itoa(-0) = %s\n", itoa(-0,buf)); */

/* 	return 0; */
/* } */
