#ifndef __CEP_PLATFORM_H__
#define __CEP_PLATFORM_H__ 

// Peripherals Addresses
#define PERIPHS_BASE_ADDRESS            0x30000000
#define REG_LEDS_ADDR                   (PERIPHS_BASE_ADDRESS + 0x0)
#define REG_LEDS_CTRL                   (PERIPHS_BASE_ADDRESS + 0x4)
#define REG_PIN_ADDR                    (PERIPHS_BASE_ADDRESS + 0x8)
#define REG_PUSHBUTTON_CTL_ADDR         (PERIPHS_BASE_ADDRESS + 0xC)

// VRAM addresses
#define FRAME_BUFFER_CTRL_BASE_ADDRESS  0x70000000
#define FRAME_BUFFER_CTRL_MODE_REG      (FRAME_BUFFER_CTRL_BASE_ADDRESS + 0x0)
#define FRAME_BUFFER_CTRL_ADDR_REG      (FRAME_BUFFER_CTRL_BASE_ADDRESS + 0x4)
#define VRAM_OFFSET                     0x80000000

// PLIC registers addresses
#define PLIC_PENDING_1                  0x0C001000
#define PLIC_ENABLE_1                   0x0c002000
#define PLIC_IRQ_CLAIM                  0x0c200004

// DEPRECATED: Gestion des priorités du PLIC a été retirée de la plateforme cep dans qemu
//#define PLIC_THRESHOLD                  0x0c200000
//#define PLIC_IRQ_PRIORITY_BASE          0x0c000000
//#define PLIC_IRQ_PRIORITY_1             0x0c000004

// CLINT registers addresses
#define CLINT_MSIP                      0x02000000
#define CLINT_TIMER_CMP                 0x02004000
#define CLINT_TIMER_CMP_HI              0x02004004
#define CLINT_TIMER_CMP_LO              0x02004000
#define CLINT_TIMER                     0x0200bff8
#define CLINT_TIMER_HI                  0x0200bffc
#define CLINT_TIMER_LOW                 0x0200bff8

// VRAM sizes
#define VRAM_WIDTH                      1280
#define VRAM_HEIGTH                     720
#define PIXEL_SIZE                      4
#define VRAM_SIZE                       (VRAM_WIDTH * VRAM_HEIGTH * PIXEL_SIZE)

// Push button modes
#define REG_PUSHBUTTON_MODE_POLL        0x0
#define REG_PUSHBUTTON_MODE_INT         0x1

// HDMI Modes
#define HDMI_MODE_720p_60Hz             4
#define HDMI_MODE_1080p_60Hz            19
#define HDMI_MODE_1080p_30Hz_blk_0      32
#define HDMI_MODE_1080p_30Hz_blk_1      33
#define HDMI_MODE_1080p_30Hz_blk_2      34

#endif // __CEP_PLATFORM_H__
