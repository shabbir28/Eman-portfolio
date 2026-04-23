import React from 'react';
import { motion } from 'framer-motion';

const GlassCard = ({ children, className = '', style = {}, hover = true, onClick }) => {
  return (
    <motion.div
      whileHover={hover ? { y: -6, scale: 1.01 } : {}}
      transition={{ type: 'spring', stiffness: 300, damping: 20 }}
      onClick={onClick}
      className={`glass-card ${className}`}
      style={{
        borderRadius: '20px',
        padding: '1.5rem',
        position: 'relative',
        overflow: 'hidden',
        cursor: onClick ? 'pointer' : 'default',
        ...style,
      }}
    >
      {/* Subtle border glow on top */}
      <div
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: '1px',
          background:
            'linear-gradient(90deg, transparent, rgba(139,92,246,0.4), rgba(59,130,246,0.4), transparent)',
          opacity: 0.5,
        }}
      />
      {children}
    </motion.div>
  );
};

export default GlassCard;
