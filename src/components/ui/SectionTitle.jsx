import React from 'react';
import { motion } from 'framer-motion';

const SectionTitle = ({ label, title, subtitle, center = true }) => {
  return (
    <div style={{ textAlign: center ? 'center' : 'left', marginBottom: '3.25rem' }}>
      {label && (
        <motion.span
          initial={{ opacity: 0, y: 15, scale: 0.9 }}
          whileInView={{ opacity: 1, y: 0, scale: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5, ease: 'easeOut' }}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontSize: '0.6875rem',
            fontWeight: 700,
            letterSpacing: '0.2em',
            textTransform: 'uppercase',
            color: '#a78bfa',
            background: 'rgba(139,92,246,0.1)',
            border: '1px solid rgba(139,92,246,0.25)',
            padding: '0.4rem 1rem',
            borderRadius: '100px',
            marginBottom: '1.25rem',
            backdropFilter: 'blur(10px)',
          }}
        >
          <motion.span
            animate={{ opacity: [1, 0.4, 1] }}
            transition={{ duration: 2, repeat: Infinity }}
            style={{
              width: 6,
              height: 6,
              borderRadius: '50%',
              background: '#8b5cf6',
              boxShadow: '0 0 10px #8b5cf6',
            }}
          />
          {label}
        </motion.span>
      )}

      <motion.h2
        initial={{ opacity: 0, y: 25 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.6, delay: 0.1, ease: 'easeOut' }}
        style={{
          fontFamily: 'Space Grotesk, sans-serif',
          fontSize: 'clamp(2rem, 5vw, 3.5rem)',
          fontWeight: 800,
          lineHeight: 1.1,
          marginBottom: '1rem',
          background: 'linear-gradient(135deg, #f8fafc 30%, #94a3b8 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text',
          letterSpacing: '-0.02em',
        }}
      >
        {title}
      </motion.h2>

      {subtitle && (
        <motion.p
          initial={{ opacity: 0, y: 15 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6, delay: 0.2, ease: 'easeOut' }}
          style={{
            color: '#94a3b8',
            fontSize: 'clamp(0.9rem, 2vw, 1.05rem)',
            maxWidth: '650px',
            margin: center ? '0 auto' : '0',
            lineHeight: 1.7,
          }}
        >
          {subtitle}
        </motion.p>
      )}

      {/* Decorative gradient line */}
      <motion.div
        initial={{ scaleX: 0 }}
        whileInView={{ scaleX: 1 }}
        viewport={{ once: true }}
        transition={{ duration: 0.7, delay: 0.3, ease: 'easeOut' }}
        style={{
          height: '3px',
          width: '70px',
          background: 'linear-gradient(90deg, #8b5cf6, #3b82f6)',
          borderRadius: '2px',
          margin: center ? '1.75rem auto 0' : '1.75rem 0 0',
          transformOrigin: center ? 'center' : 'left',
          boxShadow: '0 0 15px rgba(139,92,246,0.4)',
        }}
      />
    </div>
  );
};

export default SectionTitle;
