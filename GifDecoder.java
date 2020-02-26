/*     */ package dcdmod.Patches;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Pixmap;
/*     */ import com.badlogic.gdx.graphics.Pixmap.Format;
/*     */ import com.badlogic.gdx.graphics.Texture;
/*     */ import com.badlogic.gdx.graphics.g2d.Animation;
/*     */ import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
/*     */ import com.badlogic.gdx.graphics.g2d.TextureRegion;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GifDecoder
/*     */ {
/*     */   public static final int STATUS_OK = 0;
/*     */   public static final int STATUS_FORMAT_ERROR = 1;
/*     */   public static final int STATUS_OPEN_ERROR = 2;
/*     */   protected static final int MAX_STACK_SIZE = 4096;
/*     */   protected InputStream in;
/*     */   protected int status;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected boolean gctFlag;
/*     */   protected int gctSize;
/*  34 */   protected int loopCount = 1;
/*     */   protected int[] gct;
/*     */   protected int[] lct;
/*     */   protected int[] act;
/*     */   protected int bgIndex;
/*     */   protected int bgColor;
/*     */   protected int lastBgColor;
/*     */   protected int pixelAspect;
/*     */   protected boolean lctFlag;
/*     */   protected boolean interlace;
/*     */   protected int lctSize;
/*     */   protected int ix;
/*     */   protected int iy;
/*     */   protected int iw;
/*     */   protected int ih;
/*  49 */   protected int lrx; protected int lry; protected int lrw; protected int lrh; protected DixieMap image; protected DixieMap lastPixmap; protected byte[] block = new byte['Ā'];
/*  50 */   protected int blockSize = 0;
/*  51 */   protected int dispose = 0;
/*  52 */   protected int lastDispose = 0;
/*  53 */   protected boolean transparency = false;
/*  54 */   protected int delay = 0;
/*     */   protected int transIndex;
/*     */   protected short[] prefix;
/*     */   protected byte[] suffix;
/*     */   protected byte[] pixelStack;
/*     */   protected byte[] pixels;
/*     */   protected Vector<GifFrame> frames;
/*     */   protected int frameCount;
/*     */   
/*     */   private static class DixieMap extends Pixmap
/*     */   {
/*     */     DixieMap(int w, int h, Pixmap.Format f) {
/*  66 */       super(h, f);
/*     */     }
/*     */     
/*     */     DixieMap(int[] data, int w, int h, Pixmap.Format f) {
/*  70 */       super(h, f);
/*     */       
/*     */ 
/*     */ 
/*  74 */       for (int y = 0; y < h; y++) {
/*  75 */         for (int x = 0; x < w; x++) {
/*  76 */           int pxl_ARGB8888 = data[(x + y * w)];
/*  77 */           int pxl_RGBA8888 = pxl_ARGB8888 >> 24 & 0xFF | pxl_ARGB8888 << 8 & 0xFF00;
/*     */           
/*     */ 
/*  80 */           drawPixel(x, y, pxl_RGBA8888);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
/*  86 */       ByteBuffer bb = getPixels();
/*     */       
/*     */ 
/*     */ 
/*  90 */       for (int k = y; k < y + height; k++) {
/*  91 */         int _offset = offset;
/*  92 */         for (int l = x; l < x + width; l++) {
/*  93 */           int pxl = bb.getInt(4 * (l + k * width));
/*     */           
/*     */ 
/*  96 */           pixels[(_offset++)] = (pxl >> 8 & 0xFFFFFF | pxl << 24 & 0xFF000000);
/*     */         }
/*  98 */         offset += stride;
/*     */       }
/*     */     } }
/*     */   
/*     */   private static class GifFrame { public GifDecoder.DixieMap image;
/*     */     public int delay;
/*     */     
/* 105 */     public GifFrame(GifDecoder.DixieMap im, int del) { this.image = im;
/* 106 */       this.delay = del;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDelay(int n)
/*     */   {
/* 121 */     this.delay = -1;
/* 122 */     if ((n >= 0) && (n < this.frameCount)) {
/* 123 */       this.delay = ((GifFrame)this.frames.elementAt(n)).delay;
/*     */     }
/* 125 */     return this.delay;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getFrameCount()
/*     */   {
/* 134 */     return this.frameCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Pixmap getPixmap()
/*     */   {
/* 143 */     return getFrame(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLoopCount()
/*     */   {
/* 152 */     return this.loopCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setPixels()
/*     */   {
/* 160 */     int[] dest = new int[this.width * this.height];
/*     */     
/* 162 */     if (this.lastDispose > 0) {
/* 163 */       if (this.lastDispose == 3)
/*     */       {
/* 165 */         int n = this.frameCount - 2;
/* 166 */         if (n > 0) {
/* 167 */           this.lastPixmap = getFrame(n - 1);
/*     */         } else {
/* 169 */           this.lastPixmap = null;
/*     */         }
/*     */       }
/* 172 */       if (this.lastPixmap != null) {
/* 173 */         this.lastPixmap.getPixels(dest, 0, this.width, 0, 0, this.width, this.height);
/*     */         
/* 175 */         if (this.lastDispose == 2)
/*     */         {
/* 177 */           int c = 0;
/* 178 */           if (!this.transparency) {
/* 179 */             c = this.lastBgColor;
/*     */           }
/* 181 */           for (int i = 0; i < this.lrh; i++) {
/* 182 */             int n1 = (this.lry + i) * this.width + this.lrx;
/* 183 */             int n2 = n1 + this.lrw;
/* 184 */             for (int k = n1; k < n2; k++) {
/* 185 */               dest[k] = c;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 192 */     int pass = 1;
/* 193 */     int inc = 8;
/* 194 */     int iline = 0;
/* 195 */     for (int i = 0; i < this.ih; i++) {
/* 196 */       int line = i;
/* 197 */       if (this.interlace) {
/* 198 */         if (iline >= this.ih) {
/* 199 */           pass++;
/* 200 */           switch (pass) {
/*     */           case 2: 
/* 202 */             iline = 4;
/* 203 */             break;
/*     */           case 3: 
/* 205 */             iline = 2;
/* 206 */             inc = 4;
/* 207 */             break;
/*     */           case 4: 
/* 209 */             iline = 1;
/* 210 */             inc = 2;
/* 211 */             break;
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 216 */         line = iline;
/* 217 */         iline += inc;
/*     */       }
/* 219 */       line += this.iy;
/* 220 */       if (line < this.height) {
/* 221 */         int k = line * this.width;
/* 222 */         int dx = k + this.ix;
/* 223 */         int dlim = dx + this.iw;
/* 224 */         if (k + this.width < dlim) {
/* 225 */           dlim = k + this.width;
/*     */         }
/* 227 */         int sx = i * this.iw;
/* 228 */         while (dx < dlim)
/*     */         {
/* 230 */           int index = this.pixels[(sx++)] & 0xFF;
/* 231 */           int c = this.act[index];
/* 232 */           if (c != 0) {
/* 233 */             dest[dx] = c;
/*     */           }
/* 235 */           dx++;
/*     */         }
/*     */       }
/*     */     }
/* 239 */     this.image = new DixieMap(dest, this.width, this.height, Pixmap.Format.RGBA8888);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DixieMap getFrame(int n)
/*     */   {
/* 249 */     if (this.frameCount <= 0)
/* 250 */       return null;
/* 251 */     n %= this.frameCount;
/* 252 */     return ((GifFrame)this.frames.elementAt(n)).image;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(InputStream is)
/*     */   {
/* 263 */     init();
/* 264 */     if (is != null) {
/* 265 */       this.in = is;
/* 266 */       readHeader();
/* 267 */       if (!err()) {
/* 268 */         readContents();
/* 269 */         if (this.frameCount < 0) {
/* 270 */           this.status = 1;
/*     */         }
/*     */       }
/*     */     } else {
/* 274 */       this.status = 2;
/*     */     }
/*     */     try {
/* 277 */       is.close();
/*     */     }
/*     */     catch (Exception localException) {}
/* 280 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void decodeBitmapData()
/*     */   {
/* 287 */     int nullCode = -1;
/* 288 */     int npix = this.iw * this.ih;
/*     */     
/* 290 */     if ((this.pixels == null) || (this.pixels.length < npix)) {
/* 291 */       this.pixels = new byte[npix];
/*     */     }
/* 293 */     if (this.prefix == null) {
/* 294 */       this.prefix = new short['က'];
/*     */     }
/* 296 */     if (this.suffix == null) {
/* 297 */       this.suffix = new byte['က'];
/*     */     }
/* 299 */     if (this.pixelStack == null) {
/* 300 */       this.pixelStack = new byte['ခ'];
/*     */     }
/*     */     
/* 303 */     int data_size = read();
/* 304 */     int clear = 1 << data_size;
/* 305 */     int end_of_information = clear + 1;
/* 306 */     int available = clear + 2;
/* 307 */     int old_code = nullCode;
/* 308 */     int code_size = data_size + 1;
/* 309 */     int code_mask = (1 << code_size) - 1;
/* 310 */     for (int code = 0; code < clear; code++) {
/* 311 */       this.prefix[code] = 0;
/* 312 */       this.suffix[code] = ((byte)code); }
/*     */     int bi;
/*     */     int pi;
/* 315 */     int top; int first; int count; int bits; int datum = bits = count = first = top = pi = bi = 0;
/* 316 */     for (int i = 0; i < npix;)
/* 317 */       if (top == 0) {
/* 318 */         if (bits < code_size)
/*     */         {
/* 320 */           if (count == 0)
/*     */           {
/* 322 */             count = readBlock();
/* 323 */             if (count <= 0) {
/*     */               break;
/*     */             }
/* 326 */             bi = 0;
/*     */           }
/* 328 */           datum += ((this.block[bi] & 0xFF) << bits);
/* 329 */           bits += 8;
/* 330 */           bi++;
/* 331 */           count--;
/*     */         }
/*     */         else
/*     */         {
/* 335 */           code = datum & code_mask;
/* 336 */           datum >>= code_size;
/* 337 */           bits -= code_size;
/*     */           
/* 339 */           if ((code > available) || (code == end_of_information)) {
/*     */             break;
/*     */           }
/* 342 */           if (code == clear)
/*     */           {
/* 344 */             code_size = data_size + 1;
/* 345 */             code_mask = (1 << code_size) - 1;
/* 346 */             available = clear + 2;
/* 347 */             old_code = nullCode;
/*     */ 
/*     */           }
/* 350 */           else if (old_code == nullCode) {
/* 351 */             this.pixelStack[(top++)] = this.suffix[code];
/* 352 */             old_code = code;
/* 353 */             first = code;
/*     */           }
/*     */           else {
/* 356 */             int in_code = code;
/* 357 */             if (code == available) {
/* 358 */               this.pixelStack[(top++)] = ((byte)first);
/* 359 */               code = old_code;
/*     */             }
/* 361 */             while (code > clear) {
/* 362 */               this.pixelStack[(top++)] = this.suffix[code];
/* 363 */               code = this.prefix[code];
/*     */             }
/* 365 */             first = this.suffix[code] & 0xFF;
/*     */             
/* 367 */             if (available >= 4096) {
/*     */               break;
/*     */             }
/* 370 */             this.pixelStack[(top++)] = ((byte)first);
/* 371 */             this.prefix[available] = ((short)old_code);
/* 372 */             this.suffix[available] = ((byte)first);
/* 373 */             available++;
/* 374 */             if (((available & code_mask) == 0) && (available < 4096)) {
/* 375 */               code_size++;
/* 376 */               code_mask += available;
/*     */             }
/* 378 */             old_code = in_code;
/*     */           }
/*     */         }
/* 381 */       } else { top--;
/* 382 */         this.pixels[(pi++)] = this.pixelStack[top];
/* 383 */         i++;
/*     */       }
/* 385 */     for (i = pi; i < npix; i++) {
/* 386 */       this.pixels[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean err()
/*     */   {
/* 394 */     return this.status != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void init()
/*     */   {
/* 401 */     this.status = 0;
/* 402 */     this.frameCount = 0;
/* 403 */     this.frames = new Vector();
/* 404 */     this.gct = null;
/* 405 */     this.lct = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int read()
/*     */   {
/* 412 */     int curByte = 0;
/*     */     try {
/* 414 */       curByte = this.in.read();
/*     */     } catch (Exception e) {
/* 416 */       this.status = 1;
/*     */     }
/* 418 */     return curByte;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int readBlock()
/*     */   {
/* 427 */     this.blockSize = read();
/* 428 */     int n = 0;
/* 429 */     if (this.blockSize > 0) {
/*     */       try {
/* 431 */         int count = 0;
/* 432 */         while (n < this.blockSize) {
/* 433 */           count = this.in.read(this.block, n, this.blockSize - n);
/* 434 */           if (count == -1) {
/*     */             break;
/*     */           }
/* 437 */           n += count;
/*     */         }
/*     */       } catch (Exception e) {
/* 440 */         e.printStackTrace();
/*     */       }
/* 442 */       if (n < this.blockSize) {
/* 443 */         this.status = 1;
/*     */       }
/*     */     }
/* 446 */     return n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int[] readColorTable(int ncolors)
/*     */   {
/* 457 */     int nbytes = 3 * ncolors;
/* 458 */     int[] tab = null;
/* 459 */     byte[] c = new byte[nbytes];
/* 460 */     int n = 0;
/*     */     try {
/* 462 */       n = this.in.read(c);
/*     */     } catch (Exception e) {
/* 464 */       e.printStackTrace();
/*     */     }
/* 466 */     if (n < nbytes) {
/* 467 */       this.status = 1;
/*     */     } else {
/* 469 */       tab = new int['Ā'];
/* 470 */       int i = 0;
/* 471 */       int j = 0;
/* 472 */       while (i < ncolors) {
/* 473 */         int r = c[(j++)] & 0xFF;
/* 474 */         int g = c[(j++)] & 0xFF;
/* 475 */         int b = c[(j++)] & 0xFF;
/* 476 */         tab[(i++)] = (0xFF000000 | r << 16 | g << 8 | b);
/*     */       }
/*     */     }
/* 479 */     return tab;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readContents()
/*     */   {
/* 487 */     boolean done = false;
/* 488 */     while ((!done) && (!err())) {
/* 489 */       int code = read();
/* 490 */       switch (code) {
/*     */       case 44: 
/* 492 */         readBitmap();
/* 493 */         break;
/*     */       case 33: 
/* 495 */         code = read();
/* 496 */         switch (code) {
/*     */         case 249: 
/* 498 */           readGraphicControlExt();
/* 499 */           break;
/*     */         case 255: 
/* 501 */           readBlock();
/* 502 */           String app = "";
/* 503 */           for (int i = 0; i < 11; i++) {
/* 504 */             app = app + (char)this.block[i];
/*     */           }
/* 506 */           if (app.equals("NETSCAPE2.0")) {
/* 507 */             readNetscapeExt();
/*     */           } else {
/* 509 */             skip();
/*     */           }
/* 511 */           break;
/*     */         case 254: 
/* 513 */           skip();
/* 514 */           break;
/*     */         case 1: 
/* 516 */           skip();
/* 517 */           break;
/*     */         default: 
/* 519 */           skip();
/*     */         }
/* 521 */         break;
/*     */       case 59: 
/* 523 */         done = true;
/* 524 */         break;
/*     */       case 0: 
/*     */       default: 
/* 527 */         this.status = 1;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void readGraphicControlExt()
/*     */   {
/* 536 */     read();
/* 537 */     int packed = read();
/* 538 */     this.dispose = ((packed & 0x1C) >> 2);
/* 539 */     if (this.dispose == 0) {
/* 540 */       this.dispose = 1;
/*     */     }
/* 542 */     this.transparency = ((packed & 0x1) != 0);
/* 543 */     this.delay = (readShort() * 10);
/* 544 */     this.transIndex = read();
/* 545 */     read();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void readHeader()
/*     */   {
/* 552 */     String id = "";
/* 553 */     for (int i = 0; i < 6; i++) {
/* 554 */       id = id + (char)read();
/*     */     }
/* 556 */     if (!id.startsWith("GIF")) {
/* 557 */       this.status = 1;
/* 558 */       return;
/*     */     }
/* 560 */     readLSD();
/* 561 */     if ((this.gctFlag) && (!err())) {
/* 562 */       this.gct = readColorTable(this.gctSize);
/* 563 */       this.bgColor = this.gct[this.bgIndex];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void readBitmap()
/*     */   {
/* 571 */     this.ix = readShort();
/* 572 */     this.iy = readShort();
/* 573 */     this.iw = readShort();
/* 574 */     this.ih = readShort();
/* 575 */     int packed = read();
/* 576 */     this.lctFlag = ((packed & 0x80) != 0);
/* 577 */     this.lctSize = ((int)Math.pow(2.0D, (packed & 0x7) + 1));
/*     */     
/*     */ 
/*     */ 
/* 581 */     this.interlace = ((packed & 0x40) != 0);
/* 582 */     if (this.lctFlag) {
/* 583 */       this.lct = readColorTable(this.lctSize);
/* 584 */       this.act = this.lct;
/*     */     } else {
/* 586 */       this.act = this.gct;
/* 587 */       if (this.bgIndex == this.transIndex) {
/* 588 */         this.bgColor = 0;
/*     */       }
/*     */     }
/* 591 */     int save = 0;
/* 592 */     if (this.transparency) {
/* 593 */       save = this.act[this.transIndex];
/* 594 */       this.act[this.transIndex] = 0;
/*     */     }
/* 596 */     if (this.act == null) {
/* 597 */       this.status = 1;
/*     */     }
/* 599 */     if (err()) {
/* 600 */       return;
/*     */     }
/* 602 */     decodeBitmapData();
/* 603 */     skip();
/* 604 */     if (err()) {
/* 605 */       return;
/*     */     }
/* 607 */     this.frameCount += 1;
/*     */     
/* 609 */     this.image = new DixieMap(this.width, this.height, Pixmap.Format.RGBA8888);
/* 610 */     setPixels();
/* 611 */     this.frames.addElement(new GifFrame(this.image, this.delay));
/*     */     
/* 613 */     if (this.transparency) {
/* 614 */       this.act[this.transIndex] = save;
/*     */     }
/* 616 */     resetFrame();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readLSD()
/*     */   {
/* 624 */     this.width = readShort();
/* 625 */     this.height = readShort();
/*     */     
/* 627 */     int packed = read();
/* 628 */     this.gctFlag = ((packed & 0x80) != 0);
/*     */     
/*     */ 
/* 631 */     this.gctSize = (2 << (packed & 0x7));
/* 632 */     this.bgIndex = read();
/* 633 */     this.pixelAspect = read();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void readNetscapeExt()
/*     */   {
/*     */     do
/*     */     {
/* 641 */       readBlock();
/* 642 */       if (this.block[0] == 1)
/*     */       {
/* 644 */         int b1 = this.block[1] & 0xFF;
/* 645 */         int b2 = this.block[2] & 0xFF;
/* 646 */         this.loopCount = (b2 << 8 | b1);
/*     */       }
/* 648 */     } while ((this.blockSize > 0) && (!err()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int readShort()
/*     */   {
/* 656 */     return read() | read() << 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void resetFrame()
/*     */   {
/* 663 */     this.lastDispose = this.dispose;
/* 664 */     this.lrx = this.ix;
/* 665 */     this.lry = this.iy;
/* 666 */     this.lrw = this.iw;
/* 667 */     this.lrh = this.ih;
/* 668 */     this.lastPixmap = this.image;
/* 669 */     this.lastBgColor = this.bgColor;
/* 670 */     this.dispose = 0;
/* 671 */     this.transparency = false;
/* 672 */     this.delay = 0;
/* 673 */     this.lct = null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void skip()
/*     */   {
/*     */     do
/*     */     {
/* 681 */       readBlock();
/* 682 */     } while ((this.blockSize > 0) && (!err()));
/*     */   }
/*     */   
/*     */   public Animation<TextureRegion> getAnimation(Animation.PlayMode playMode) {
/* 686 */     int nrFrames = getFrameCount();
/* 687 */     Pixmap frame = getFrame(0);
/* 688 */     int width = frame.getWidth();
/* 689 */     int height = frame.getHeight();
/* 690 */     int vzones = (int)Math.sqrt(nrFrames);
/* 691 */     int hzones = vzones;
/*     */     
/* 693 */     while (vzones * hzones < nrFrames) { vzones++;
/*     */     }
/*     */     
/*     */ 
/* 697 */     Pixmap target = new Pixmap(width * hzones, height * vzones, Pixmap.Format.RGBA8888);
/*     */     
/* 699 */     for (int h = 0; h < hzones; h++) {
/* 700 */       for (int v = 0; v < vzones; v++) {
/* 701 */         int frameID = v + h * vzones;
/* 702 */         if (frameID < nrFrames) {
/* 703 */           frame = getFrame(frameID);
/* 704 */           target.drawPixmap(frame, h * width, v * height);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 709 */     Texture texture = new Texture(target);
/* 710 */     Array<TextureRegion> texReg = new Array();
/*     */     
/* 712 */     for (h = 0; h < hzones; h++) {
/* 713 */       for (int v = 0; v < vzones; v++) {
/* 714 */         int frameID = v + h * vzones;
/* 715 */         if (frameID < nrFrames) {
/* 716 */           TextureRegion tr = new TextureRegion(texture, h * width, v * height, width, height);
/* 717 */           texReg.add(tr);
/*     */         }
/*     */       }
/*     */     }
/* 721 */     float frameDuration = getDelay(0);
/* 722 */     frameDuration /= 1000.0F;
/* 723 */     Animation<TextureRegion> result = new Animation(frameDuration, texReg, playMode);
/*     */     
/* 725 */     return result;
/*     */   }
/*     */   
/*     */   public static Animation<TextureRegion> loadGIFAnimation(Animation.PlayMode playMode, InputStream is) {
/* 729 */     GifDecoder gdec = new GifDecoder();
/* 730 */     gdec.read(is);
/* 731 */     return gdec.getAnimation(playMode);
/*     */   }
/*     */   
/*     */   public static Animation<TextureRegion> loadGIFAnimation(Animation.PlayMode playMode, InputStream is, GifAnimation animation) {
/* 735 */     GifDecoder gdec = new GifDecoder();
/* 736 */     gdec.read(is);
/* 737 */     animation.gdec = gdec;
/* 738 */     return gdec.getAnimation(playMode);
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 742 */     return this.width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 746 */     return this.height;
/*     */   }
/*     */ }


/* Location:              D:\eg.MOD\mod\DCD.jar!\dcdmod\Patches\GifDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */