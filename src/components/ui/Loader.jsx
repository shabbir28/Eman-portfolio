import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const Loader = () => {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval);
          return 100;
        }
        return prev + Math.random() * 15;
      });
    }, 120);
    return () => clearInterval(interval);
  }, []);

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        transition={{ duration: 0.5 }}
        style={{
          position: 'fixed',
          inset: 0,
          background: '#0a0a0f',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 99999,
        }}
      >
        {/* Animated Gradient Orbs */}
        <div
          style={{
            position: 'absolute',
            width: '300px',
            height: '300px',
            background:
              'radial-gradient(circle, rgba(139,92,246,0.25) 0%, transparent 70%)',
            borderRadius: '50%',
            filter: 'blur(40px)',
            animation: 'float 8s ease-in-out infinite',
          }}
        />
        <div
          style={{
            position: 'absolute',
            width: '250px',
            height: '250px',
            background:
              'radial-gradient(circle, rgba(59,130,246,0.2) 0%, transparent 70%)',
            borderRadius: '50%',
            filter: 'blur(40px)',
            animation: 'float 10s ease-in-out infinite reverse',
            bottom: '20%',
            right: '20%',
          }}
        />

        {/* Loader content */}
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.6, ease: 'backOut' }}
          style={{ textAlign: 'center' }}
        >
          {/* Spinning ring */}
          <div style={{ position: 'relative', width: 100, height: 100, margin: '0 auto 2rem' }}>
            <motion.div
              animate={{ rotate: 360 }}
              transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
              style={{
                position: 'absolute',
                inset: 0,
                borderRadius: '50%',
                border: '3px solid transparent',
                borderTopColor: 'rgba(139,92,246,0.8)',
                borderRightColor: 'rgba(59,130,246,0.6)',
              }}
            />
            <motion.div
              animate={{ rotate: -360 }}
              transition={{ duration: 1.5, repeat: Infinity, ease: 'linear' }}
              style={{
                position: 'absolute',
                inset: 8,
                borderRadius: '50%',
                border: '2px solid transparent',
                borderTopColor: 'rgba(6,182,212,0.7)',
                borderBottomColor: 'rgba(139,92,246,0.5)',
              }}
            />
            <div
              style={{
                position: 'absolute',
                inset: 16,
                borderRadius: '50%',

                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '1.75rem',
                fontWeight: 800,
                fontFamily: 'Space Grotesk, sans-serif',
                background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
              }}
            >
              E
            </div>
          </div>

          <motion.h1
            initial={{ y: 20, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ delay: 0.3 }}
            style={{
              fontFamily: 'Space Grotesk, sans-serif',
              fontSize: '2rem',
              fontWeight: 700,
              background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              marginBottom: '0.5rem',
              letterSpacing: '-0.02em',
            }}
          >
            Eman.
          </motion.h1>

          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.5 }}
            style={{
              color: '#64748b',
              fontSize: '0.875rem',
              marginBottom: '2rem',
            }}
          >
            Software Engineer & Designer
          </motion.p>

          {/* Progress bar */}
          <div
            style={{
              width: '180px',
              height: '3px',
              background: 'rgba(255,255,255,0.08)',
              borderRadius: '2px',
              overflow: 'hidden',
              margin: '0 auto',
            }}
          >
            <motion.div
              style={{
                height: '100%',
                background:
                  'linear-gradient(90deg, #8b5cf6, #3b82f6, #06b6d4)',
                borderRadius: '2px',
                width: `${Math.min(progress, 100)}%`,
                boxShadow: '0 0 15px rgba(139,92,246,0.5)',
              }}
              transition={{ duration: 0.1 }}
            />
          </div>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
};

export default Loader;
