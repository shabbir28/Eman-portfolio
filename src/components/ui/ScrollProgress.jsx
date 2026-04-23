import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';

const ScrollProgress = () => {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      setProgress(docHeight > 0 ? (scrollTop / docHeight) * 100 : 0);
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        height: '3px',
        background: 'rgba(255,255,255,0.04)',
        zIndex: 9998,
      }}
    >
      <motion.div
        style={{
          height: '100%',
          width: `${progress}%`,
          background:
            'linear-gradient(90deg, #8b5cf6, #3b82f6, #06b6d4)',
          boxShadow: '0 0 15px rgba(139,92,246,0.6), 0 0 30px rgba(59,130,246,0.3)',
          borderRadius: '0 2px 2px 0',
        }}
        transition={{ duration: 0.08, ease: 'linear' }}
      />
    </div>
  );
};

export default ScrollProgress;
