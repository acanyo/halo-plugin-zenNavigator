/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "../src/main/resources/templates/**/*.html",
    "../src/main/resources/static/js/**/*.js",
    "../ui/src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f7ff',
          100: '#e0effe',
          200: '#bae0fd',
          300: '#90cafc',
          400: '#5eacf9',
          500: '#3b8ef5',
          600: '#2570ea',
          700: '#1d58d8',
          800: '#1e47af',
          900: '#1e3f8a',
          950: '#172654',
        },
        secondary: {
          50: '#ecfdf5',
          100: '#d1fae5',
          200: '#a7f3d0',
          300: '#6ee7b7',
          400: '#34d399',
          500: '#10b981',
          600: '#059669',
          700: '#047857',
          800: '#065f46',
          900: '#064e3b',
          950: '#022c22',
        }
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out forwards',
        'slide-up': 'slideUp 0.3s ease-out forwards',
        'scale-in': 'scaleIn 0.3s ease-out forwards',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'spin-slow': 'spin 3s linear infinite',
        'bounce-slow': 'bounce 3s infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        scaleIn: {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
      },
      spacing: {
        '18': '4.5rem',
        '72': '18rem',
        '84': '21rem',
        '96': '24rem',
      },
      fontSize: {
        'xxs': '0.625rem',
      },
      borderRadius: {
        'xl': '1rem',
        '2xl': '1.5rem',
        '3xl': '2rem',
      },
      boxShadow: {
        'soft': '0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04)',
        'inner-soft': 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',
      },
      transitionDuration: {
        '400': '400ms',
      },
      backdropBlur: {
        'xs': '2px',
      },
      maxWidth: {
        '8xl': '90rem',
        '9xl': '100rem',
      },
      zIndex: {
        '60': '60',
        '70': '70',
        '80': '80',
        '90': '90',
        '100': '100',
      },
      fontFamily: {
        sans: ['"PingFang SC"', '"Helvetica Neue"', 'Arial', 'sans-serif'],
      },
    }
  },
  variants: {
    extend: {
      opacity: ['dark', 'group-hover', 'focus-within'],
      backgroundColor: ['dark', 'active', 'group-hover'],
      textColor: ['dark', 'active', 'group-hover'],
      borderColor: ['dark', 'active', 'focus-within'],
      ringColor: ['dark', 'hover', 'active'],
      ringOpacity: ['dark', 'hover', 'active'],
      ringOffsetWidth: ['dark', 'hover', 'active'],
      ringOffsetColor: ['dark', 'hover', 'active'],
      boxShadow: ['dark', 'hover', 'active', 'focus-within'],
      transform: ['hover', 'focus', 'active', 'group-hover'],
      scale: ['hover', 'focus', 'active', 'group-hover'],
      translate: ['hover', 'focus', 'active', 'group-hover'],
      rotate: ['hover', 'focus', 'active', 'group-hover'],
    }
  },
  plugins: [
    // 添加自定义插件函数
    function({ addUtilities }) {
      const newUtilities = {
        '.text-shadow-sm': {
          textShadow: '0 1px 2px rgba(0, 0, 0, 0.1)',
        },
        '.text-shadow': {
          textShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        },
        '.text-shadow-md': {
          textShadow: '0 4px 8px rgba(0, 0, 0, 0.12), 0 2px 4px rgba(0, 0, 0, 0.08)',
        },
        '.text-shadow-lg': {
          textShadow: '0 15px 30px rgba(0, 0, 0, 0.11), 0 5px 15px rgba(0, 0, 0, 0.08)',
        },
        '.text-shadow-none': {
          textShadow: 'none',
        },
      }
      addUtilities(newUtilities)
    }
  ]
} 