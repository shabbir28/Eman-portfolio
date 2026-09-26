import React from 'react';
import { motion } from 'framer-motion';
import { Heart, ArrowUp, Mail } from 'lucide-react';
import { FaGithub, FaLinkedin, FaFacebook } from 'react-icons/fa';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const socialLinks = [
    { icon: FaGithub, href: 'https://github.com/eman-110', label: 'GitHub', color: '#f1f5f9' },
    { icon: FaLinkedin, href: 'https://www.linkedin.com/in/eman-abdul-malik-90b042333/', label: 'LinkedIn', color: '#0077b5' },
    { icon: FaFacebook, href: 'https://www.facebook.com/share/17ErLnE3ya/?mibextid=wwXIfr', label: 'Facebook', color: '#1877f2' },
    { icon: Mail, href: 'mailto:emanabdulmalik18@gmail.com', label: 'Email', color: '#8b5cf6' },
  ];

  const footerLinks = [
    { label: 'Home', href: '#hero' },
    { label: 'About', href: '#about' },
    { label: 'Projects', href: '#projects' },
    { label: 'Contact', href: '#contact' },
  ];

  return (
    <footer
      style={{
        position: 'relative',
        background:
          'linear-gradient(180deg, rgba(10,10,15,0.9) 0%, rgba(7,7,20,0.95) 100%)',
        borderTop: '1px solid rgba(255,255,255,0.08)',
        padding: '4rem 2rem 2rem',
        backdropFilter: 'blur(20px)',
      }}
    >
      {/* Background glow */}
      <div
        style={{
          position: 'absolute',
          top: 0,
          left: '50%',
          transform: 'translateX(-50%)',
          width: '60vw',
          height: '200px',
          background:
            'radial-gradient(ellipse at center top, rgba(139,92,246,0.1) 0%, transparent 70%)',
          pointerEvents: 'none',
          filter: 'blur(60px)',
        }}
      />

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.6 }}
        style={{
          maxWidth: '1200px',
          margin: '0 auto',
          position: 'relative',
          zIndex: 1,
        }}
      >
        {/* Main footer content */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '3rem',
            marginBottom: '3rem',
          }}
        >
          {/* Brand column */}
          <div style={{ textAlign: 'left' }}>
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.1 }}
              style={{
                fontFamily: 'Space Grotesk, sans-serif',
                fontSize: '1.75rem',
                fontWeight: 700,
                background: 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                marginBottom: '0.75rem',
                letterSpacing: '-0.02em',
              }}
            >
              Eman.
            </motion.div>
            <p
              style={{
                color: '#64748b',
                fontSize: '0.875rem',
                lineHeight: 1.7,
                maxWidth: '280px',
              }}
            >
              Crafting digital experiences with code and creativity. Building the
              future, one line at a time.
            </p>
          </div>

          {/* Quick links */}
          <div>
            <h4
              style={{
                fontFamily: 'Space Grotesk, sans-serif',
                fontWeight: 600,
                color: '#f1f5f9',
                fontSize: '0.875rem',
                marginBottom: '1.25rem',
                letterSpacing: '0.05em',
                textTransform: 'uppercase',
              }}
            >
              Quick Links
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.625rem' }}>
              {footerLinks.map((link) => (
                <motion.a
                  key={link.label}
                  href={link.href}
                  whileHover={{ x: 4 }}
                  style={{
                    color: '#64748b',
                    textDecoration: 'none',
                    fontSize: '0.875rem',
                    transition: 'color 0.2s ease',
                    width: 'fit-content',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.color = '#8b5cf6';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = '#64748b';
                  }}
                >
                  {link.label}
                </motion.a>
              ))}
            </div>
          </div>

          {/* Contact info */}
          <div>
            <h4
              style={{
                fontFamily: 'Space Grotesk, sans-serif',
                fontWeight: 600,
                color: '#f1f5f9',
                fontSize: '0.875rem',
                marginBottom: '1.25rem',
                letterSpacing: '0.05em',
                textTransform: 'uppercase',
              }}
            >
              Connect
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <a
                href="mailto:emanabdulmalik18@gmail.com"
                style={{
                  color: '#64748b',
                  textDecoration: 'none',
                  fontSize: '0.875rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  transition: 'color 0.2s ease',
                  overflowWrap: 'anywhere',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.color = '#8b5cf6';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.color = '#64748b';
                }}
              >
                <Mail size={14} />
                emanabdulmalik18@gmail.com
              </a>
              <span
                style={{
                  color: '#64748b',
                  fontSize: '0.875rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                }}
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
                Available Remotely
              </span>
            </div>
          </div>

          {/* Social column */}
          <div>
            <h4
              style={{
                fontFamily: 'Space Grotesk, sans-serif',
                fontWeight: 600,
                color: '#f1f5f9',
                fontSize: '0.875rem',
                marginBottom: '1.25rem',
                letterSpacing: '0.05em',
                textTransform: 'uppercase',
              }}
            >
              Follow Me
            </h4>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              {socialLinks.map(({ icon: Icon, href, label, color }) => (
                <motion.a
                  key={label}
                  href={href}
                  target={href.startsWith('http') ? '_blank' : undefined}
                  rel={href.startsWith('http') ? 'noopener noreferrer' : undefined}
                  aria-label={label}
                  whileHover={{ scale: 1.15, y: -3 }}
                  whileTap={{ scale: 0.9 }}
                  className="social-link"
                  style={{
                    width: 44,
                    height: 44,
                    borderRadius: '12px',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.background = `${color}15`;
                    e.currentTarget.style.borderColor = `${color}40`;
                    e.currentTarget.style.color = color;
                    e.currentTarget.style.boxShadow = `0 8px 20px ${color}20`;
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.background = 'rgba(255,255,255,0.05)';
                    e.currentTarget.style.borderColor = 'rgba(255,255,255,0.1)';
                    e.currentTarget.style.color = '#64748b';
                    e.currentTarget.style.boxShadow = 'none';
                  }}
                >
                  <Icon size={18} />
                </motion.a>
              ))}
            </div>
          </div>
        </div>

        {/* Divider */}
        <div
          style={{
            height: '1px',
            background:
              'linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent)',
            marginBottom: '2rem',
          }}
        />

        {/* Bottom bar */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: '1rem',
          }}
        >
          <p
            style={{
              color: '#64748b',
              fontSize: '0.8125rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
            }}
          >
            © {currentYear} Eman. Made with
            <motion.span
              animate={{ scale: [1, 1.2, 1] }}
              transition={{ duration: 1.5, repeat: Infinity }}
              style={{ display: 'inline-flex' }}
            >
              <Heart size={14} fill="#8b5cf6" color="#8b5cf6" />
            </motion.span>
            All rights reserved.
          </p>

          {/* Back to top button */}
          <motion.button
            onClick={scrollToTop}
            whileHover={{ y: -3 }}
            whileTap={{ scale: 0.95 }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.625rem 1.25rem',
              background: 'rgba(255,255,255,0.05)',
              border: '1px solid rgba(255,255,255,0.1)',
              borderRadius: '100px',
              color: '#94a3b8',
              fontSize: '0.8125rem',
              fontWeight: 500,
              cursor: 'pointer',
              transition: 'all 0.3s ease',
              backdropFilter: 'blur(10px)',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = 'rgba(139,92,246,0.15)';
              e.currentTarget.style.borderColor = 'rgba(139,92,246,0.4)';
              e.currentTarget.style.color = '#8b5cf6';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = 'rgba(255,255,255,0.05)';
              e.currentTarget.style.borderColor = 'rgba(255,255,255,0.1)';
              e.currentTarget.style.color = '#94a3b8';
            }}
          >
            <ArrowUp size={16} />
            Back to top
          </motion.button>
        </div>
      </motion.div>
    </footer>
  );
};

export default Footer;
