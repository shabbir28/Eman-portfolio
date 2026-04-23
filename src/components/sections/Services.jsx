import React, { useState } from 'react';
import { motion } from 'framer-motion';
import SectionTitle from '../ui/SectionTitle';
import {
  Smartphone,
  Globe,
  Palette,
  Brush,
  LayoutTemplate,
  Code2,
  ArrowRight,
} from 'lucide-react';

const services = [
  {
    icon: Smartphone,
    title: 'Mobile Development',
    desc: 'Building performant, cross-platform mobile apps using React Native for iOS and Android.',
    color: '#8b5cf6',
    gradient: 'linear-gradient(135deg, rgba(139,92,246,0.2), rgba(124,58,237,0.08))',
  },
  {
    icon: Globe,
    title: 'Web Development',
    desc: 'Crafting responsive, modern web applications using React, HTML, CSS, and JavaScript.',
    color: '#3b82f6',
    gradient: 'linear-gradient(135deg, rgba(59,130,246,0.2), rgba(37,99,235,0.08))',
  },
  {
    icon: Palette,
    title: 'UI/UX Design',
    desc: 'Designing intuitive, user-centered interfaces with Figma and Adobe XD for any platform.',
    color: '#06b6d4',
    gradient: 'linear-gradient(135deg, rgba(6,182,212,0.2), rgba(8,145,178,0.08))',
  },
  {
    icon: Brush,
    title: 'Brand Design',
    desc: 'Creating cohesive brand identities—logos, color systems, and visual language.',
    color: '#f59e0b',
    gradient: 'linear-gradient(135deg, rgba(245,158,11,0.2), rgba(217,119,6,0.08))',
  },
  {
    icon: LayoutTemplate,
    title: 'WordPress Development',
    desc: 'Building polished, conversion-focused WordPress websites with custom themes.',
    color: '#ec4899',
    gradient: 'linear-gradient(135deg, rgba(236,72,153,0.2), rgba(219,39,119,0.08))',
  },
  {
    icon: Code2,
    title: 'Custom Software',
    desc: 'Developing tailored desktop and enterprise solutions using Java and MySQL.',
    color: '#10b981',
    gradient: 'linear-gradient(135deg, rgba(16,185,129,0.2), rgba(5,150,105,0.08))',
  },
];

const Services = () => {
  const [hovered, setHovered] = useState(null);

  const containerVariants = {
    hidden: {},
    visible: { transition: { staggerChildren: 0.08 } },
  };

  const cardVariants = {
    hidden: { opacity: 0, y: 50, scale: 0.95 },
    visible: { opacity: 1, y: 0, scale: 1, transition: { duration: 0.6, ease: 'easeOut' } },
  };

  return (
    <section
      id="services"
      style={{
        padding: 'clamp(3rem, 6vw, 5rem) clamp(1rem, 5vw, 3rem)',
        position: 'relative',
      }}
    >
      {/* Background gradient */}
      <div
        style={{
          position: 'absolute',
          top: '20%',
          right: '-10%',
          width: '60vw',
          height: '60vh',
          background:
            'radial-gradient(ellipse at center, rgba(6,182,212,0.08) 0%, transparent 60%)',
          pointerEvents: 'none',
        }}
      />

      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <SectionTitle
          label="Services"
          title="What I Offer"
          subtitle="End-to-end solutions from design to deployment—tailored to your needs."
        />

        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, margin: '-60px' }}
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '1.5rem',
          }}
        >
          {services.map((svc) => (
            <motion.div
              key={svc.title}
              variants={cardVariants}
              onHoverStart={() => setHovered(svc.title)}
              onHoverEnd={() => setHovered(null)}
            >
              <motion.div
                whileHover={{ y: -10, scale: 1.02 }}
                transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                style={{
                  background: hovered === svc.title ? svc.gradient : 'rgba(255,255,255,0.03)',
                  border: `1px solid ${hovered === svc.title ? svc.color + '50' : 'rgba(255,255,255,0.08)'}`,
                  borderRadius: '20px',
                  padding: '2rem',
                  height: '100%',
                  position: 'relative',
                  overflow: 'hidden',
                  transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
                  boxShadow: hovered === svc.title
                    ? `0 25px 70px ${svc.color}20`
                    : 'none',
                  cursor: 'default',
                }}
              >
                {/* Top gradient line */}
                <div
                  style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    height: '2px',
                    background: `linear-gradient(90deg, transparent, ${svc.color}80, transparent)`,
                    opacity: hovered === svc.title ? 1 : 0,
                    transition: 'opacity 0.3s ease',
                  }}
                />

                {/* Icon */}
                <motion.div
                  animate={
                    hovered === svc.title
                      ? { rotate: [0, -10, 10, 0], scale: 1.1 }
                      : { rotate: 0, scale: 1 }
                  }
                  transition={{ duration: 0.5 }}
                  style={{
                    width: 56,
                    height: 56,
                    borderRadius: '16px',
                    background: `${svc.color}15`,
                    border: `1px solid ${svc.color}30`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    marginBottom: '1.5rem',
                    backdropFilter: 'blur(10px)',
                  }}
                >
                  <svc.icon size={26} style={{ color: svc.color }} />
                </motion.div>

                <h3
                  style={{
                    fontFamily: 'Space Grotesk, sans-serif',
                    fontWeight: 700,
                    fontSize: '1.125rem',
                    color: '#f1f5f9',
                    marginBottom: '0.75rem',
                    letterSpacing: '-0.01em',
                  }}
                >
                  {svc.title}
                </h3>
                <p
                  style={{
                    color: '#64748b',
                    fontSize: '0.875rem',
                    lineHeight: 1.7,
                  }}
                >
                  {svc.desc}
                </p>

                {/* Arrow indicator on hover */}
                <motion.div
                  initial={{ opacity: 0, x: -10 }}
                  animate={
                    hovered === svc.title
                      ? { opacity: 1, x: 0 }
                      : { opacity: 0, x: -10 }
                  }
                  transition={{ duration: 0.25 }}
                  style={{
                    marginTop: '1.5rem',
                    color: svc.color,
                    fontSize: '0.8125rem',
                    fontWeight: 600,
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.4rem',
                  }}
                >
                  Learn more
                  <ArrowRight size={14} />
                </motion.div>
              </motion.div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
};

export default Services;
