import React, { useState } from 'react';
import { motion } from 'framer-motion';
import SectionTitle from '../ui/SectionTitle';
import { Send, MapPin, Clock, Mail, CheckCircle, Loader2 } from 'lucide-react';
import { FaGithub, FaLinkedin, FaFacebook } from 'react-icons/fa';

const socials = [
  { icon: FaGithub, href: 'https://github.com/eman-110', label: 'GitHub', color: '#f1f5f9' },
  { icon: FaLinkedin, href: 'https://www.linkedin.com/in/eman-abdul-malik-90b042333/', label: 'LinkedIn', color: '#0077b5' },
  { icon: FaFacebook, href: 'https://www.facebook.com/share/17ErLnE3ya/?mibextid=wwXIfr', label: 'Facebook', color: '#1877f2' },
];

const info = [
  { icon: Mail, label: 'Email', value: 'emanabdulmalik18@gmail.com', color: '#8b5cf6' },
  { icon: MapPin, label: 'Location', value: 'Available Remotely', color: '#3b82f6' },
  { icon: Clock, label: 'Response Time', value: 'Within 24 Hours', color: '#06b6d4' },
];

const Contact = () => {
  const [form, setForm] = useState({
    name: '',
    email: '',
    subject: '',
    message: '',
  });
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');
  const [focused, setFocused] = useState(null);

  const handleChange = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSending(true);
    setError('');
    try {
      const res = await fetch('https://formsubmit.co/ajax/emanabdulmalik18@gmail.com', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify({
          name: form.name,
          email: form.email,
          subject: form.subject,
          message: form.message,
          _captcha: 'false',
        }),
      });
      if (res.ok) {
        setSending(false);
        setSent(true);
        setTimeout(() => setSent(false), 4000);
        setForm({ name: '', email: '', subject: '', message: '' });
      } else {
        throw new Error('Failed');
      }
    } catch {
      setSending(false);
      setError('Message could not be sent. Email me directly and I will get back to you.');
    }
  };

  return (
    <section
      id="contact"
      style={{
        padding: 'clamp(3rem, 6vw, 5rem) clamp(1rem, 5vw, 3rem)',
        position: 'relative',
      }}
    >
      {/* Animated background elements */}
      <div
        style={{
          position: 'absolute',
          top: '20%',
          left: '50%',
          transform: 'translateX(-50%)',
          width: '100vw',
          height: '60vh',
          background:
            'radial-gradient(ellipse at center, rgba(139,92,246,0.1) 0%, transparent 60%)',
          pointerEvents: 'none',
        }}
      />

      {/* Decorative orbs */}
      <motion.div
        animate={{
          y: [0, -30, 0],
          x: [0, 20, 0],
        }}
        transition={{
          duration: 8,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
        style={{
          position: 'absolute',
          top: '30%',
          left: '10%',
          width: '200px',
          height: '200px',
          background:
            'radial-gradient(circle, rgba(139,92,246,0.15) 0%, transparent 70%)',
          borderRadius: '50%',
          pointerEvents: 'none',
        }}
      />
      <motion.div
        animate={{
          y: [0, 30, 0],
          x: [0, -20, 0],
        }}
        transition={{
          duration: 10,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
        style={{
          position: 'absolute',
          bottom: '20%',
          right: '5%',
          width: '250px',
          height: '250px',
          background:
            'radial-gradient(circle, rgba(59,130,246,0.12) 0%, transparent 70%)',
          borderRadius: '50%',
          pointerEvents: 'none',
        }}
      />

      <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
        <SectionTitle
          label="Contact"
          title="Let's Build Something Great"
          subtitle="Have a project in mind? Let's collaborate and turn your vision into reality."
        />

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
            gap: '2.5rem',
            alignItems: 'start',
          }}
        >
          {/* Left - Contact Info */}
          <motion.div
            initial={{ opacity: 0, x: -40 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, ease: 'easeOut' }}
            style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}
          >
            {/* Info card */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.1 }}
              style={{
                background: 'rgba(255,255,255,0.03)',
                border: '1px solid rgba(255,255,255,0.08)',
                borderRadius: '20px',
                padding: '2rem',
                backdropFilter: 'blur(20px)',
              }}
            >
              <h3
                style={{
                  fontFamily: 'Space Grotesk, sans-serif',
                  fontWeight: 700,
                  fontSize: '1.25rem',
                  color: '#f1f5f9',
                  marginBottom: '0.5rem',
                }}
              >
                Get in Touch
              </h3>
              <p
                style={{
                  color: '#64748b',
                  fontSize: '0.9rem',
                  lineHeight: 1.7,
                  marginBottom: '1.75rem',
                }}
              >
                I&apos;m always excited to discuss new projects, creative ideas, or
                opportunities to be part of your vision.
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {info.map(({ icon: Icon, label, value, color }) => (
                  <motion.div
                    key={label}
                    whileHover={{ x: 4 }}
                    style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}
                  >
                    <div
                      style={{
                        width: 48,
                        height: 48,
                        borderRadius: '12px',
                        background: `${color}15`,
                        border: `1px solid ${color}30`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        backdropFilter: 'blur(10px)',
                      }}
                    >
                      <Icon size={20} style={{ color }} />
                    </div>
                    <div>
                      <div
                        style={{
                          fontSize: '0.7rem',
                          color: '#475569',
                          fontWeight: 600,
                          textTransform: 'uppercase',
                          letterSpacing: '0.1em',
                        }}
                      >
                        {label}
                      </div>
                      <div style={{ color: '#e2e8f0', fontSize: '0.9rem', fontWeight: 500, overflowWrap: 'anywhere' }}>
                        {label === 'Email' ? (
                          <a href={`mailto:${value}`} style={{ color: 'inherit', textDecoration: 'none' }}>
                            {value}
                          </a>
                        ) : (
                          value
                        )}
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>
            </motion.div>

            {/* Social links */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.2 }}
              style={{
                background: 'rgba(255,255,255,0.03)',
                border: '1px solid rgba(255,255,255,0.08)',
                borderRadius: '20px',
                padding: '1.5rem',
                backdropFilter: 'blur(20px)',
              }}
            >
              <p
                style={{
                  color: '#475569',
                  fontSize: '0.75rem',
                  fontWeight: 600,
                  textTransform: 'uppercase',
                  letterSpacing: '0.15em',
                  marginBottom: '1rem',
                }}
              >
                Connect With Me
              </p>
              <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                {socials.map(({ icon: Icon, href, label, color }) => (
                  <motion.a
                    key={label}
                    href={href}
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label={label}
                    whileHover={{ scale: 1.15, y: -3 }}
                    whileTap={{ scale: 0.9 }}
                    className="social-link"
                    style={{
                      width: 48,
                      height: 48,
                      borderRadius: '12px',
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.background = `${color}15`;
                      e.currentTarget.style.borderColor = `${color}40`;
                      e.currentTarget.style.color = color;
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.background = 'rgba(255,255,255,0.05)';
                      e.currentTarget.style.borderColor = 'rgba(255,255,255,0.1)';
                      e.currentTarget.style.color = '#64748b';
                    }}
                  >
                    <Icon size={18} />
                  </motion.a>
                ))}
              </div>
            </motion.div>
          </motion.div>

          {/* Right - Contact Form */}
          <motion.div
            initial={{ opacity: 0, x: 40 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: 0.1, ease: 'easeOut' }}
          >
            <motion.form
              className="contact-form"
              onSubmit={handleSubmit}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              style={{
                background: 'rgba(255,255,255,0.03)',
                border: '1px solid rgba(255,255,255,0.08)',
                borderRadius: '20px',
                padding: '2rem',
                backdropFilter: 'blur(20px)',
                display: 'flex',
                flexDirection: 'column',
                gap: '1rem',
                position: 'relative',
                overflow: 'hidden',
              }}
            >
              {/* Decorative gradient line */}
              <div
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  height: '2px',
                  background:
                    'linear-gradient(90deg, transparent, #8b5cf6, #3b82f6, transparent)',
                  opacity: 0.5,
                }}
              />

              {/* Row: Name + Email */}
              <div className="form-row">
                <div className="input-group">
                  <input
                    id="name"
                    name="name"
                    type="text"
                    placeholder=" "
                    required
                    value={form.name}
                    onChange={handleChange}
                    onFocus={() => setFocused('name')}
                    onBlur={() => setFocused(null)}
                    className="input-field"
                    style={{
                      background: focused === 'name'
                        ? 'rgba(139,92,246,0.08)'
                        : 'rgba(255,255,255,0.04)',
                    }}
                  />
                  <label htmlFor="name" className="input-label">
                    Your Name
                  </label>
                </div>

                <div className="input-group">
                  <input
                    id="email"
                    name="email"
                    type="email"
                    placeholder=" "
                    required
                    value={form.email}
                    onChange={handleChange}
                    onFocus={() => setFocused('email')}
                    onBlur={() => setFocused(null)}
                    className="input-field"
                    style={{
                      background: focused === 'email'
                        ? 'rgba(139,92,246,0.08)'
                        : 'rgba(255,255,255,0.04)',
                    }}
                  />
                  <label htmlFor="email" className="input-label">
                    Email Address
                  </label>
                </div>
              </div>

              {/* Subject */}
              <div className="input-group">
                <input
                  id="subject"
                  name="subject"
                  type="text"
                  placeholder=" "
                  required
                  value={form.subject}
                  onChange={handleChange}
                  onFocus={() => setFocused('subject')}
                  onBlur={() => setFocused(null)}
                  className="input-field"
                />
                <label htmlFor="subject" className="input-label">
                  Subject
                </label>
              </div>

              {/* Message */}
              <div className="input-group">
                <textarea
                  id="message"
                  name="message"
                  rows={5}
                  placeholder=" "
                  required
                  value={form.message}
                  onChange={handleChange}
                  onFocus={() => setFocused('message')}
                  onBlur={() => setFocused(null)}
                  className="input-field"
                  style={{
                    resize: 'vertical',
                    minHeight: '130px',
                  }}
                />
                <label htmlFor="message" className="input-label">
                  Your Message
                </label>
              </div>

              {/* Submit Button */}
              {error && (
                <p role="alert" style={{ color: '#fca5a5', fontSize: '0.875rem', lineHeight: 1.5, margin: 0 }}>
                  {error}
                </p>
              )}

              <motion.button
                type="submit"
                disabled={sending || sent}
                whileHover={{ scale: sending || sent ? 1 : 1.02 }}
                whileTap={{ scale: sending || sent ? 1 : 0.98 }}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '0.75rem',
                  padding: '1rem 2rem',
                  background: sent
                    ? 'linear-gradient(135deg, #10b981, #059669)'
                    : 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                  border: 'none',
                  borderRadius: '12px',
                  color: '#fff',
                  fontSize: '0.9375rem',
                  fontWeight: 600,
                  cursor: sending ? 'wait' : 'pointer',
                  fontFamily: 'Inter, sans-serif',
                  transition: 'all 0.3s ease',
                  boxShadow: sent
                    ? '0 8px 30px rgba(16,185,129,0.3)'
                    : '0 8px 30px rgba(139,92,246,0.4)',
                  position: 'relative',
                  overflow: 'hidden',
                }}
              >
                {sending ? (
                  <>
                    <Loader2
                      size={18}
                      className="spinner"
                      style={{
                        animation: 'spin 1s linear infinite',
                      }}
                    />
                    Sending...
                  </>
                ) : sent ? (
                  <>
                    <CheckCircle size={18} />
                    Message Sent Successfully!
                  </>
                ) : (
                  <>
                    <Send size={18} />
                    Send Message
                  </>
                )}
              </motion.button>
            </motion.form>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default Contact;
