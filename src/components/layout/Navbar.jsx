import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Menu, X } from 'lucide-react';

const navLinks = [
  { id: 'hero', label: 'Home' },
  { id: 'about', label: 'About' },
  { id: 'skills', label: 'Skills' },
  { id: 'projects', label: 'Projects' },
  { id: 'services', label: 'Services' },
  { id: 'contact', label: 'Contact' },
];

const Navbar = () => {
  const [scrolled, setScrolled] = useState(false);
  const [active, setActive] = useState('hero');
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 30);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => {
    document.body.style.overflow = menuOpen ? 'hidden' : '';
    return () => {
      document.body.style.overflow = '';
    };
  }, [menuOpen]);

  useEffect(() => {
    const onKey = (event) => {
      if (event.key === 'Escape') setMenuOpen(false);
    };
    const onResize = () => {
      if (window.innerWidth > 768) setMenuOpen(false);
    };
    window.addEventListener('keydown', onKey);
    window.addEventListener('resize', onResize);
    return () => {
      window.removeEventListener('keydown', onKey);
      window.removeEventListener('resize', onResize);
    };
  }, []);

  useEffect(() => {
    const observers = navLinks.map(({ id }) => {
      const el = document.getElementById(id);
      if (!el) return null;
      const observer = new IntersectionObserver(
        ([entry]) => { if (entry.isIntersecting) setActive(id); },
        { rootMargin: '-40% 0px -55% 0px' }
      );
      observer.observe(el);
      return observer;
    });
    return () => observers.forEach(obs => obs?.disconnect());
  }, []);

  const scrollTo = (id) => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
    setMenuOpen(false);
  };

  return (
    <>
      <motion.nav
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.8, ease: 'easeOut' }}
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          zIndex: 1000,
          padding: '0.75rem 2rem',
          height: '70px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: scrolled
            ? 'rgba(10, 10, 15, 0.8)'
            : 'rgba(10, 10, 15, 0.4)',
          backdropFilter: scrolled ? 'blur(20px)' : 'blur(10px)',
          WebkitBackdropFilter: scrolled ? 'blur(20px)' : 'blur(10px)',
          borderBottom: scrolled
            ? '1px solid rgba(255,255,255,0.08)'
            : '1px solid rgba(255,255,255,0.05)',
          transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
        }}
      >
        {/* Logo */}
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => scrollTo('hero')}
          style={{
            border: 'none',
            cursor: 'pointer',
            fontFamily: 'Space Grotesk, sans-serif',
            fontSize: '1.75rem',
            fontWeight: 700,
            background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            backgroundClip: 'text',
            letterSpacing: '-0.02em',
          }}
        >
          Eman<span style={{ WebkitTextFillColor: '#8b5cf6' }}></span>
        </motion.button>

        {/* Desktop Navigation */}
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }} className="hidden-mobile">
          {navLinks.map(({ id, label }) => (
            <motion.button
              key={id}
              onClick={() => scrollTo(id)}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              style={{
                border: active === id
                  ? '1px solid rgba(139, 92, 246, 0.4)'
                  : '1px solid transparent',
                borderRadius: '100px',
                padding: '0.5rem 1.25rem',
                cursor: 'pointer',
                color: active === id ? '#fff' : '#94a3b8',
                fontSize: '0.875rem',
                fontWeight: active === id ? 600 : 500,
                background: active === id
                  ? 'rgba(139, 92, 246, 0.15)'
                  : 'transparent',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                fontFamily: 'Inter, sans-serif',
                position: 'relative',
              }}
            >
              {active === id && (
                <motion.div
                  layoutId="nav-indicator"
                  style={{
                    position: 'absolute',
                    inset: 0,
                    borderRadius: '100px',
                    background: 'rgba(139, 92, 246, 0.2)',
                    border: '1px solid rgba(139, 92, 246, 0.4)',
                    zIndex: -1,
                  }}
                />
              )}
              {label}
            </motion.button>
          ))}

          {/* CTA Button */}
          <motion.button
            onClick={() => scrollTo('contact')}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            style={{
              marginLeft: '0.75rem',
              background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
              border: 'none',
              borderRadius: '100px',
              padding: '0.625rem 1.5rem',
              cursor: 'pointer',
              color: '#fff',
              fontSize: '0.875rem',
              fontWeight: 600,
              fontFamily: 'Inter, sans-serif',
              boxShadow: '0 4px 15px rgba(139, 92, 246, 0.3)',
              transition: 'box-shadow 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.boxShadow =
                '0 6px 25px rgba(139, 92, 246, 0.5)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.boxShadow =
                '0 4px 15px rgba(139, 92, 246, 0.3)';
            }}
          >
            Let&apos;s Talk
          </motion.button>
        </div>

        {/* Mobile Menu Toggle */}
        <motion.button
          onClick={() => setMenuOpen(v => !v)}
          className="show-mobile"
          whileTap={{ scale: 0.9 }}
          style={{
            background: 'rgba(255,255,255,0.08)',
            border: '1px solid rgba(255,255,255,0.15)',
            borderRadius: '12px',
            width: 44,
            height: 44,
            display: 'none',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            color: '#fff',
            backdropFilter: 'blur(10px)',
          }}
        >
          {menuOpen ? <X size={20} /> : <Menu size={20} />}
        </motion.button>
      </motion.nav>

      {/* Mobile Menu */}
      <AnimatePresence>
        {menuOpen && (
          <motion.div
            initial={{ opacity: 0, y: -20, height: 0 }}
            animate={{ opacity: 1, y: 0, height: 'auto' }}
            exit={{ opacity: 0, y: -20, height: 0 }}
            transition={{ duration: 0.3, ease: 'easeInOut' }}
            style={{
              position: 'fixed',
              top: '70px',
              left: 0,
              right: 0,
              background: 'rgba(10, 10, 15, 0.95)',
              backdropFilter: 'blur(20px)',
              WebkitBackdropFilter: 'blur(20px)',
              borderBottom: '1px solid rgba(255,255,255,0.08)',
              zIndex: 999,
              padding: '1rem 2rem',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.5rem',
              overflow: 'hidden',
            }}
          >
            {navLinks.map(({ id, label }, index) => (
              <motion.button
                key={id}
                onClick={() => scrollTo(id)}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }}
                style={{
                  background: active === id
                    ? 'rgba(139, 92, 246, 0.15)'
                    : 'transparent',
                  border: 'none',
                  textAlign: 'left',
                  padding: '0.875rem 1rem',
                  cursor: 'pointer',
                  color: active === id ? '#fff' : '#94a3b8',
                  fontSize: '1rem',
                  fontWeight: active === id ? 600 : 500,
                  fontFamily: 'Inter, sans-serif',
                  borderRadius: '10px',
                  transition: 'all 0.2s ease',
                }}
              >
                {label}
              </motion.button>
            ))}
            <motion.button
              type="button"
              onClick={() => scrollTo('contact')}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: navLinks.length * 0.05 }}
              style={{
                marginTop: '0.25rem',
                background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                border: 'none',
                textAlign: 'center',
                padding: '0.875rem 1rem',
                cursor: 'pointer',
                color: '#fff',
                fontSize: '1rem',
                fontWeight: 600,
                fontFamily: 'Inter, sans-serif',
                borderRadius: '10px',
              }}
            >
              Let&apos;s Talk
            </motion.button>
          </motion.div>
        )}
      </AnimatePresence>

      <style>{`
        @media (max-width: 768px) {
          .hidden-mobile { display: none !important; }
          .show-mobile { display: flex !important; }
        }
        @media (min-width: 769px) {
          .hidden-mobile { display: flex !important; }
          .show-mobile { display: none !important; }
        }
      `}</style>
    </>
  );
};

export default Navbar;
