import React, { useEffect, useRef, useState } from 'react';
import { motion } from 'framer-motion';
import { gsap } from 'gsap';
import { ArrowDown, Download } from 'lucide-react';
import { FaGithub, FaLinkedin } from 'react-icons/fa';
import cvFile from '../../assets/CV - Eman.pdf';

const ROLES = ['Software Developer', 'Mobile App Developer', 'Desktop App Developer'];

const Hero = () => {
  const nameRef = useRef(null);
  const [roleIndex, setRoleIndex] = useState(0);
  const [displayed, setDisplayed] = useState('');
  const [deleting, setDeleting] = useState(false);
  const [showCursor, setShowCursor] = useState(true);

  // GSAP letter stagger for "Eman"
  useEffect(() => {
    if (!nameRef.current) return;
    const letters = nameRef.current.querySelectorAll('.letter');
    gsap.fromTo(
      letters,
      { y: 80, opacity: 0, rotateX: -90, scale: 0.8 },
      {
        y: 0,
        opacity: 1,
        rotateX: 0,
        scale: 1,
        duration: 0.8,
        stagger: 0.1,
        ease: 'back.out(1.7)',
        delay: 0.3,
      }
    );
  }, []);

  // Blinking cursor effect
  useEffect(() => {
    const cursorInterval = setInterval(() => {
      setShowCursor(prev => !prev);
    }, 530);
    return () => clearInterval(cursorInterval);
  }, []);

  // Typewriter effect
  useEffect(() => {
    const current = ROLES[roleIndex];
    let timeout;
    if (!deleting) {
      if (displayed.length < current.length) {
        timeout = setTimeout(() => setDisplayed(current.slice(0, displayed.length + 1)), 90);
      } else {
        timeout = setTimeout(() => setDeleting(true), 2200);
      }
    } else {
      if (displayed.length > 0) {
        timeout = setTimeout(() => setDisplayed(displayed.slice(0, -1)), 40);
      } else {
        timeout = setTimeout(() => {
          setDeleting(false);
          setRoleIndex(i => (i + 1) % ROLES.length);
        }, 500);
      }
    }
    return () => clearTimeout(timeout);
  }, [displayed, deleting, roleIndex]);

  const scrollTo = (id) => document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });

  return (
    <section
      id="hero"
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        overflow: 'hidden',
        padding: '0 1.5rem',
        paddingBottom: '7rem',
        textAlign: 'center',
      }}
    >
      {/* Animated Mesh Gradient Background */}
      <div className="animated-bg">
        <div className="mesh-gradient" />
        <div
          className="gradient-orb"
          style={{
            top: '10%',
            left: '15%',
            width: '500px',
            height: '500px',
            background: 'radial-gradient(circle, rgba(139, 92, 246, 0.25) 0%, transparent 70%)',
            animationDelay: '0s',
          }}
        />
        <div
          className="gradient-orb"
          style={{
            top: '60%',
            right: '10%',
            width: '400px',
            height: '400px',
            background: 'radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%)',
            animationDelay: '4s',
          }}
        />
        <div
          className="gradient-orb"
          style={{
            bottom: '20%',
            left: '30%',
            width: '350px',
            height: '350px',
            background: 'radial-gradient(circle, rgba(6, 182, 212, 0.18) 0%, transparent 70%)',
            animationDelay: '8s',
          }}
        />
      </div>

      {/* Grid pattern overlay */}
      <div
        style={{
          position: 'absolute',
          inset: 0,
          backgroundImage: `linear-gradient(rgba(255,255,255,0.015) 1px, transparent 1px),
            linear-gradient(90deg, rgba(255,255,255,0.015) 1px, transparent 1px)`,
          backgroundSize: '70px 70px',
          pointerEvents: 'none',
        }}
      />

      {/* Content */}
      <div style={{ position: 'relative', zIndex: 1, maxWidth: '900px', width: '100%' }}>
        {/* Badge */}
        <motion.div
          initial={{ opacity: 0, y: 30, scale: 0.9 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          transition={{ delay: 0.2, duration: 0.6, ease: 'easeOut' }}
          whileHover={{ scale: 1.05, y: -2 }}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.5rem',
            background: 'rgba(139, 92, 246, 0.1)',
            border: '1px solid rgba(139, 92, 246, 0.3)',
            borderRadius: '100px',
            padding: '0.5rem 1.25rem',
            marginBottom: '2rem',
            fontSize: '0.75rem',
            fontWeight: 600,
            color: '#a78bfa',
            letterSpacing: '0.15em',
            backdropFilter: 'blur(10px)',
            boxShadow: '0 4px 20px rgba(139, 92, 246, 0.15)',
          }}
        >
          <motion.span
            animate={{ opacity: [1, 0.5, 1] }}
            transition={{ duration: 2, repeat: Infinity }}
            style={{
              width: 8,
              height: 8,
              borderRadius: '50%',
              background: '#8b5cf6',
              boxShadow: '0 0 12px #8b5cf6',
            }}
          />
          AVAILABLE FOR HIRING
        </motion.div>

        {/* Name with enhanced animation */}
        <div
          ref={nameRef}
          style={{
            fontFamily: 'Space Grotesk, sans-serif',
            fontSize: 'clamp(3.5rem, 12vw, 8rem)',
            fontWeight: 800,
            lineHeight: 1,
            marginBottom: '0.5rem',
            perspective: '1000px',
            letterSpacing: '-0.03em',
          }}
        >
          {'Eman'.split('').map((char, i) => (
            <motion.span
              key={i}
              className="letter"
              initial={{ y: 100, opacity: 0, rotateX: -90 }}
              animate={{ y: 0, opacity: 1, rotateX: 0 }}
              transition={{
                delay: 0.4 + i * 0.1,
                duration: 0.8,
                ease: [0.34, 1.56, 0.64, 1],
              }}
              style={{
                display: 'inline-block',
                background: 'linear-gradient(135deg, #ffffff 0%, rgba(139, 92, 246, 0.9) 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                textShadow: '0 0 40px rgba(139, 92, 246, 0.3)',
              }}
            >
              {char}
            </motion.span>
          ))}
        </div>

        {/* Typing animation with cursor */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.2, duration: 0.5 }}
          style={{
            fontFamily: 'Space Grotesk, sans-serif',
            fontSize: 'clamp(1.25rem, 3vw, 2rem)',
            fontWeight: 500,
            height: '2.5rem',
            marginBottom: '1.5rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.5rem',
          }}
        >
          <span style={{ color: '#94a3b8' }}>I&apos;m a </span>
          <span
            style={{
              background: 'linear-gradient(90deg, #8b5cf6, #3b82f6, #06b6d4)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              fontWeight: 600,
              position: 'relative',
            }}
          >
            {displayed}
            <span
              style={{
                display: 'inline-block',
                width: '3px',
                height: '1.2em',
                background: 'linear-gradient(180deg, #8b5cf6, #3b82f6)',
                marginLeft: '4px',
                verticalAlign: 'text-bottom',
                opacity: showCursor ? 1 : 0,
                transition: 'opacity 0.1s ease',
                borderRadius: '2px',
              }}
            />
          </span>
        </motion.div>

        {/* Subtext */}
        <motion.p
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1.4, duration: 0.6, ease: 'easeOut' }}
          style={{
            color: '#64748b',
            fontSize: 'clamp(0.95rem, 2vw, 1.15rem)',
            maxWidth: '600px',
            margin: '0 auto 2.5rem',
            lineHeight: 1.75,
          }}
        >
          I specialize in developing fast, scalable, and user-friendly mobile and desktop apps using modern technologies.
        </motion.p>

        {/* CTA Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1.6, duration: 0.6, ease: 'easeOut' }}
          style={{
            display: 'flex',
            gap: '1rem',
            justifyContent: 'center',
            flexWrap: 'wrap',
            marginBottom: '2.5rem',
          }}
        >
          <motion.a
            href={cvFile}
            download="CV-Eman.pdf"
            whileHover={{ scale: 1.05, y: -3 }}
            whileTap={{ scale: 0.97 }}
            style={{
              background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
              border: 'none',
              borderRadius: '12px',
              padding: '1rem 2.5rem',
              color: '#fff',
              fontSize: '1rem',
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif',
              boxShadow: '0 8px 30px rgba(139, 92, 246, 0.4)',
              transition: 'box-shadow 0.3s ease, transform 0.3s ease',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              textDecoration: 'none',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.boxShadow =
                '0 12px 40px rgba(139, 92, 246, 0.5)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.boxShadow =
                '0 8px 30px rgba(139, 92, 246, 0.4)';
            }}
          >
            Download CV
            <Download size={18} />
          </motion.a>

          <motion.button
            whileHover={{ scale: 1.05, y: -3 }}
            whileTap={{ scale: 0.97 }}
            onClick={() => scrollTo('contact')}
            style={{
              background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(255, 255, 255, 0.15)',
              borderRadius: '12px',
              padding: '1rem 2.5rem',
              color: '#fff',
              fontSize: '1rem',
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif',
              backdropFilter: 'blur(10px)',
              transition: 'all 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = 'rgba(139, 92, 246, 0.1)';
              e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.4)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = 'rgba(255, 255, 255, 0.05)';
              e.currentTarget.style.borderColor = 'rgba(255, 255, 255, 0.15)';
            }}
          >
            Get in Touch
          </motion.button>
        </motion.div>

        {/* Social Icons */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.8, duration: 0.6 }}
          style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}
        >
          {[
            { icon: FaGithub, href: 'https://github.com/eman-110', label: 'GitHub' },
            { icon: FaLinkedin, href: 'https://www.linkedin.com/in/eman-abdul-malik-90b042333/', label: 'LinkedIn' },
          ].map(({ icon: Icon, href, label }) => (
            <motion.a
              key={label}
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              whileHover={{ scale: 1.15, y: -3 }}
              whileTap={{ scale: 0.9 }}
              className="social-link"
              style={{
                width: 48,
                height: 48,
                borderRadius: '12px',
              }}
            >
              <Icon size={20} />
            </motion.a>
          ))}
        </motion.div>
      </div>

      {/* Scroll indicator */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 2, duration: 0.6 }}
        style={{
          position: 'absolute',
          bottom: '2rem',
          left: '50%',
          transform: 'translateX(-50%)',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '0.5rem',
          color: '#475569',
          cursor: 'pointer',
        }}
        onClick={() => scrollTo('about')}
      >
        <span style={{ fontSize: '0.65rem', letterSpacing: '0.2em', textTransform: 'uppercase' }}>
          Scroll to explore
        </span>
        <motion.div
          animate={{ y: [0, 6, 0] }}
          transition={{ repeat: Infinity, duration: 1.8, ease: 'easeInOut' }}
        >
          <ArrowDown size={18} />
        </motion.div>
      </motion.div>
    </section>
  );
};

export default Hero;
