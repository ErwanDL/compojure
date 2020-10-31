#ifndef LIBSCREEN
#define LIBSCREEN

typedef unsigned int uint;
#include "font.h"
#include "femto.h"

#define CLINT_MSIP                      0x02000000
#define CLINT_TIMER_CMP                 0x02004000
#define CLINT_TIMER_CMP_HI              0x02004004
#define CLINT_TIMER_CMP_LO              0x02004000
#define CLINT_TIMER                     0x0200bff8
#define CLINT_TIMER_HI                  0x0200bffc
#define CLINT_TIMER_LOW                 0x0200bff8



volatile uint* IMG;
volatile uint *push;
volatile uint *timer;
volatile uint *timer_cmp;

/* function to get the state of push buttons */
uint push_button_get(void);

// Timer options
#ifdef ENV_QEMU 
#define TIMER_FREQ 10000000 // 10MHz
#define RATIO 500  
#else
#define TIMER_FREQ 100000000 // 100MHz
#define RATIO 200 
#endif

/* function to set the timer to be reached in period*time/100 in the future */
void timer_set(uint period, uint time);

/* function to wait for timer zero value */
void timer_wait(void);

void timer_set_and_wait(uint period, uint time);

#define NBCOL 1920
#define NBROW 1080

uint fgcolor;
uint bgcolor;

void draw(uint color, uint x, uint y);

void clear_screen(uint color);

void draw_bitmap(char* bitmap);

void newline();
void tab();

/* Counts the number of characters of current word. Will be used to break lines, if possible not in the middle of words. */
uint num_characters_until_white(char* str);

uint display_cur_x = 0;
uint display_cur_y = 0;
uint display_scale = 16;

void display_uint(uint i);
void display_string(char* str);
void set_fg_color(uint color);
void set_bg_color(uint color);
#endif
