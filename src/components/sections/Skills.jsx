import React, { useRef, useState } from 'react';
import { motion, useInView } from 'framer-motion';
import SectionTitle from '../ui/SectionTitle';
import {
  FaJava,
  FaJs,
  FaDatabase,
  FaHtml5,
  FaCss3Alt,
  FaReact,
  FaMobileAlt,
  FaFigma,
  FaImage,
  FaPaintBrush,
  FaLeaf,
} from 'react-icons/fa';
import { Code2 } from 'lucide-react';

const skillsData = [
  {
    category: 'Programming',
    emoji: '⚡',
    color: '#8b5cf6',
    gradient: 'linear-gradient(135deg, rgba(139,92,246,0.18), rgba(124,58,237,0.06))',
    skills: [
      { name: 'Java', icon: FaJava, color: '#f59e0b' },
      { name: 'JavaScript', icon: FaJs, color: '#eab308' },
      { name: 'C++', icon: Code2, color: '#3b82f6' },
    ],
  },
  {
    category: 'Databases',
    emoji: '🗄️',
    color: '#3b82f6',
    gradient: 'linear-gradient(135deg, rgba(59,130,246,0.18), rgba(37,99,235,0.06))',
    skills: [
      { name: 'MySQL', icon: FaDatabase, color: '#00758f' },
      { name: 'MongoDB', icon: FaLeaf, color: '#00ed64' },
    ],
  },
  {
    category: 'Web & Mobile',
    emoji: '🌐',
    color: '#06b6d4',
    gradient: 'linear-gradient(135deg, rgba(6,182,212,0.18), rgba(8,145,178,0.06))',
    skills: [
      { name: 'HTML5', icon: FaHtml5, color: '#e34f26' },
      { name: 'CSS3', icon: FaCss3Alt, color: '#1572b6' },
      { name: 'React', icon: FaReact, color: '#61dafb' },
      { name: 'React Native', icon: FaMobileAlt, color: '#3b82f6' },
    ],
  },
  {
    category: 'Design Tools',
    emoji: '🎨',
    color: '#f59e0b',
    gradient: 'linear-gradient(135deg, rgba(245,158,11,0.18), rgba(217,119,6,0.06))',
    skills: [
      { name: 'Figma', icon: FaFigma, color: '#f24e1e' },
      { name: 'Adobe XD', icon: FaPaintBrush, color: '#ff61f6' },
      { name: 'Canva', icon: FaImage, color: '#00c4cc' },
      { name: 'Photoshop', icon: FaImage, color: '#31a8ff' },
    ],
  },
];

const Skills = () => {
  const [hoveredCat, setHoveredCat] = useState(null);
  const sectionRef = useRef(null);
  const isInView = useInView(sectionRef, { once: true, margin: '-80px' });

  const containerVariants = {
    hidden: {},
    visible: { transition: { staggerChildren: 0.1 } },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 40, scale: 0.96 },
    visible: { opacity: 1, y: 0, scale: 1, transition: { duration: 0.55, ease: 'easeOut' } },
  };

  return (
    <section
      id="skills"
      ref={sectionRef}
      style={{
        padding: 'clamp(3rem, 6vw, 5rem) clamp(1rem, 5vw, 3rem)',
        position: 'relative',
      }}
    >
      {/* Background glow */}
      <div
        style={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: '80vw',
          height: '80vh',
          background: 'radial-gradient(ellipse at center, rgba(139,92,246,0.07) 0%, transparent 60%)',
          pointerEvents: 'none',
        }}
      />

      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <SectionTitle
          label="Skills"
          title="Tech Stack"
          subtitle="Core technologies behind my applications"
        />

        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate={isInView ? 'visible' : 'hidden'}
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(4, 1fr)',
            gap: '1.25rem',
            alignItems: 'stretch',
          }}
        >
          {skillsData.map((cat) => (
            <motion.div
              key={cat.category}
              variants={itemVariants}
              onHoverStart={() => setHoveredCat(cat.category)}
              onHoverEnd={() => setHoveredCat(null)}
              style={{ display: 'flex' }}
            >
              <motion.div
                whileHover={{ y: -6, scale: 1.02 }}
                transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                style={{
                  flex: 1,
                  background: hoveredCat === cat.category ? cat.gradient : 'rgba(255,255,255,0.03)',
                  border: `1px solid ${hoveredCat === cat.category ? cat.color + '45' : 'rgba(255,255,255,0.08)'}`,
                  borderRadius: '20px',
                  padding: '1.75rem 1.5rem',
                  position: 'relative',
                  overflow: 'hidden',
                  transition: 'all 0.35s cubic-bezier(0.4, 0, 0.2, 1)',
                  boxShadow: hoveredCat === cat.category
                    ? `0 20px 50px ${cat.color}18`
                    : '0 2px 12px rgba(0,0,0,0.15)',
                }}
              >
                {/* Top accent line */}
                <div
                  style={{
                    position: 'absolute',
                    top: 0,
                    left: '20%',
                    right: '20%',
                    height: '2px',
                    borderRadius: '0 0 4px 4px',
                    background: `linear-gradient(90deg, transparent, ${cat.color}, transparent)`,
                    opacity: hoveredCat === cat.category ? 1 : 0.3,
                    transition: 'opacity 0.3s ease',
                  }}
                />

                {/* Glow orb */}
                <div
                  style={{
                    position: 'absolute',
                    bottom: '-30px',
                    right: '-30px',
                    width: '100px',
                    height: '100px',
                    borderRadius: '50%',
                    background: `radial-gradient(circle, ${cat.color}20, transparent 70%)`,
                    pointerEvents: 'none',
                  }}
                />

                {/* Category header */}
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.625rem',
                    marginBottom: '1.25rem',
                  }}
                >
                  <div
                    style={{
                      width: 38,
                      height: 38,
                      borderRadius: '10px',
                      background: `${cat.color}18`,
                      border: `1px solid ${cat.color}30`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '1.1rem',
                      flexShrink: 0,
                    }}
                  >
                    {cat.emoji}
                  </div>
                  <h3
                    style={{
                      fontFamily: 'Space Grotesk, sans-serif',
                      fontWeight: 700,
                      fontSize: '0.9375rem',
                      color: '#f1f5f9',
                      letterSpacing: '-0.01em',
                    }}
                  >
                    {cat.category}
                  </h3>
                </div>

                {/* Skill chips */}
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '0.625rem',
                  }}
                >
                  {cat.skills.map((skill) => (
                    <motion.div
                      key={skill.name}
                      whileHover={{ x: 4 }}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.625rem',
                        padding: '0.5rem 0.75rem',
                        background: 'rgba(255,255,255,0.04)',
                        border: '1px solid rgba(255,255,255,0.07)',
                        borderRadius: '10px',
                        transition: 'all 0.2s ease',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.background = `${skill.color}12`;
                        e.currentTarget.style.borderColor = `${skill.color}35`;
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.background = 'rgba(255,255,255,0.04)';
                        e.currentTarget.style.borderColor = 'rgba(255,255,255,0.07)';
                      }}
                    >
                      <skill.icon
                        style={{
                          color: skill.color,
                          fontSize: '1rem',
                          flexShrink: 0,
                        }}
                      />
                      <span
                        style={{
                          color: '#cbd5e1',
                          fontSize: '0.8125rem',
                          fontWeight: 500,
                        }}
                      >
                        {skill.name}
                      </span>
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
};

export default Skills;
