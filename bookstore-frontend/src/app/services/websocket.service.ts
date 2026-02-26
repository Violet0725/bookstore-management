import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { Client, Frame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/**
 * WebSocketService - Real-Time Communication with Spring Boot
 * 
 * Handles WebSocket connection using STOMP protocol over SockJS.
 * Subscribes to /topic/low-stock for real-time low stock alerts.
 * 
 * Interview Points:
 * - STOMP over WebSocket for pub/sub messaging
 * - SockJS provides fallback for browsers without WebSocket
 * - RxJS Observables for reactive data flow
 * - BehaviorSubject for connection state management
 */
@Injectable({
  providedIn: 'root'
})
export class WebSocketService implements OnDestroy {
  // WebSocket endpoint
  private readonly WS_URL = 'http://localhost:8080/ws';
  
  // Topic to subscribe to
  private readonly LOW_STOCK_TOPIC = '/topic/low-stock';
  
  // STOMP client
  private stompClient: Client | null = null;
  
  // Observable for low stock alerts
  private lowStockSubject = new Subject<string>();
  public lowStockAlerts$ = this.lowStockSubject.asObservable();
  
  // Connection status
  private connectedSubject = new BehaviorSubject<boolean>(false);
  public isConnected$ = this.connectedSubject.asObservable();

  constructor() {
    this.connect();
  }

  /**
   * Initialize WebSocket connection
   */
  connect(): void {
    // Configure STOMP client
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.WS_URL),
      reconnectDelay: 5000,  // Reconnect every 5 seconds if connection fails
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket connected');
        this.connectedSubject.next(true);
        this.subscribeToLowStock();
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
        this.connectedSubject.next(false);
      },
      onStompError: (frame: Frame) => {
        console.error('STOMP error', frame);
      }
    });

    // Activate the client
    this.stompClient.activate();
  }

  /**
   * Subscribe to low stock alerts topic
   */
  private subscribeToLowStock(): void {
    if (!this.stompClient) return;

    this.stompClient.subscribe(this.LOW_STOCK_TOPIC, (message: { body: string }) => {
      // Parse message body (text message)
      const alertMessage = message.body;
      console.log('Low stock alert received:', alertMessage);
      
      // Push to observable
      this.lowStockSubject.next(alertMessage);
    });
  }

  /**
   * Send a message to a specific destination
   * (Useful if we need to send messages TO the server)
   */
  sendMessage(destination: string, body: unknown): void {
    if (!this.stompClient || !this.stompClient.connected) {
      console.warn('WebSocket not connected');
      return;
    }

    this.stompClient.publish({
      destination: destination,
      body: JSON.stringify(body)
    });
  }

  /**
   * Disconnect WebSocket
   */
  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
    this.lowStockSubject.complete();
    this.connectedSubject.complete();
  }
}
